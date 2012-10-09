package memphis.fridge.server.services;

import java.math.BigDecimal;

import javax.inject.Inject;
import memphis.fridge.domain.Product;
import memphis.fridge.domain.User;
import memphis.fridge.exceptions.*;
import memphis.fridge.server.io.HMACResponse;
import memphis.fridge.server.io.ResponseSerializer;
import memphis.fridge.server.ioc.MockInjectingRunner;
import memphis.fridge.server.ioc.MockInjectingRunner.Mock;
import memphis.fridge.server.ioc.MockInjectingRunner.MockManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertNotNull;
import static memphis.fridge.server.TestingData.*;
import static memphis.fridge.utils.CurrencyUtils.fromCents;
import static memphis.fridge.utils.CurrencyUtils.toCents;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 6/10/12
 */
@RunWith(MockInjectingRunner.class)
@MockInjectingRunner.ToInject({Purchase.class})
public class PurchaseTest {

	private static final Object[] HMAC_ARGS = {
			SNONCE, USERNAME, order()
	};

	@Inject
	Purchase p;

	@Inject
	@Mock
	User user;

	@Inject
	@Mock
	ResponseSerializer.ObjectSerializer response;

	@Inject
	MockManager mocks;

	@Before
	public void setUp() {
		mocks.reset();
	}

	@Test(expected = FridgeException.class)
	public void testPurchaseBadHMAC() throws Exception {
		p.users.validateHMAC(USERNAME, HMAC, HMAC_ARGS);
		expectLastCall().andThrow(new FridgeException(1, "invalid hmac"));
		test();
	}

	@Test(expected = InvalidUserException.class)
	public void testPurchaseErrorGettingUser() throws Exception {
		p.users.validateHMAC(USERNAME, HMAC, HMAC_ARGS);
		expect(p.users.retrieveUser(USERNAME)).andThrow(new InvalidUserException(USERNAME));
		test();
	}

	@Test(expected = FridgeException.class)
	public void testPurchaseErrorGettingDiscount() throws Exception {
		p.users.validateHMAC(USERNAME, HMAC, HMAC_ARGS);
		expect(p.users.retrieveUser(USERNAME)).andReturn(user);
		expect(user.isGrad()).andReturn(false);
		expect(p.fridge.getGraduateDiscount()).andThrow(new FridgeException(1, "Database error"));
		test();
	}

	@Test(expected = InvalidProductException.class)
	public void testPurchaseInvalidProduct() throws Exception {
		expectUserInit(true);
		expect(p.products.findProduct("CC")).andThrow(new InvalidProductException("CC"));
		test();
	}

	@Test(expected = InsufficientStockException.class)
	public void testPurchaseInsufficientStock() throws Exception {
		Product coke = coke();
		coke.setInStock(1);

		expectUserInit(true);
		expect(p.products.findProduct("CC")).andReturn(coke);
		test();
	}

	@Test(expected = InvalidProductException.class)
	public void testPurchaseInvalidProductTwo() throws Exception {
		Product coke = coke();
		boolean isGrad = true;

		expectUserInit(isGrad);
		expect(p.products.findProduct("CC")).andReturn(coke);
		p.products.consumeProduct(coke, ORDER_NUM_COKE);

		BigDecimal num = BigDecimal.valueOf(ORDER_NUM_COKE);
		expect(p.purchases.createPurchase(user, coke, ORDER_NUM_COKE, orderCokeBase(), orderCokeTax(isGrad)))
				.andReturn(null);
		expect(p.products.findProduct("CT")).andThrow(new InvalidProductException("CT"));
		test();
	}

	@Test(expected = InsufficientFundsException.class)
	public void testPurchaseInsufficientFundsUndergrad() throws Exception {
		expectUserInit(false);
		expectProductUpdates(false);
		p.users.removeFunds(user, orderTotal(false));
		expectLastCall().andThrow(new InsufficientFundsException(USERNAME, BigDecimal.ZERO));
		test();
	}

	@Test(expected = InsufficientFundsException.class)
	public void testPurchaseInsufficientFundsGrad() throws Exception {
		expectUserInit(true);
		expectProductUpdates(true);
		p.users.removeFunds(user, orderTotal(true));
		expectLastCall().andThrow(new InsufficientFundsException(USERNAME, BigDecimal.ZERO));
		test();
	}

	@Test
	public void testPurchaseGrad() throws Exception {
		final BigDecimal balance = fromCents(1000);
		final BigDecimal cost = orderTotal(true);
		expectUserInit(true);
		expectProductUpdates(true);
		expectUserUpdates(cost);
		expectResponse(cost, balance);
		test();
	}

	@Test
	public void testPurchaseUndergrad() throws Exception {
		final BigDecimal balance = fromCents(1000);
		final BigDecimal cost = orderTotal(false);
		expectUserInit(false);
		expectProductUpdates(false);
		expectUserUpdates(cost);
		expectResponse(cost, balance);
		test();
	}

	/* Helper methods */

	private void test() {
		mocks.replay();

		try {
			HMACResponse r = p.purchase(SNONCE, USERNAME, order(), HMAC);
			assertNotNull(r);
			r.visit(response);
		} catch (FridgeException ex) {
			mocks.verify();
			throw ex;
		}
		mocks.verify();
	}

	private void expectUserInit(boolean isGrad) {
		p.users.validateHMAC(USERNAME, HMAC, HMAC_ARGS);
		expect(p.users.retrieveUser(USERNAME)).andReturn(user);
		expect(user.isGrad()).andReturn(isGrad);
		if (!isGrad) {
			expect(p.fridge.getGraduateDiscount()).andReturn(GRAD_TAX);
		}
	}

	private void expectProductUpdates(boolean isGrad) {
		Product coke = coke();
		expect(p.products.findProduct("CC")).andReturn(coke);
		p.products.consumeProduct(coke, ORDER_NUM_COKE);
		expect(p.purchases.createPurchase(user, coke, ORDER_NUM_COKE, orderCokeBase(), orderCokeTax(isGrad)))
				.andReturn(null);

		Product cookie = cookie();
		expect(p.products.findProduct("CT")).andReturn(cookie);
		p.products.consumeProduct(cookie, ORDER_NUM_COOKIE);
		expect(p.purchases.createPurchase(user, cookie, ORDER_NUM_COOKIE, orderCookieBase(), orderCookieTax(isGrad)))
				.andReturn(null);
	}

	private void expectUserUpdates(BigDecimal cost) {
		p.users.removeFunds(user, cost);
		p.creditLog.createPurchase(user, cost);
	}

	private void expectResponse(BigDecimal cost, BigDecimal balance) {
		expect(p.users.retrieveUser(USERNAME)).andReturn(user);
		expect(user.getBalance()).andReturn(BigDecimal.TEN);
		expect(p.users.createHMAC(USERNAME, SNONCE, toCents(balance), toCents(cost))).andReturn(HMAC);
		response.visitInteger("balance", toCents(balance));
		response.visitInteger("order_total", toCents(cost));
		response.visitString("hmac", HMAC);
	}
}
