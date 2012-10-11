package memphis.fridge.server.services;

import java.math.BigDecimal;

import javax.inject.Inject;
import memphis.fridge.domain.Product;
import memphis.fridge.domain.User;
import memphis.fridge.exceptions.*;
import memphis.fridge.protocol.Messages;
import memphis.fridge.server.ioc.AuthModule;
import memphis.fridge.server.ioc.MockInjectingRunner;
import memphis.fridge.server.ioc.MockInjectingRunner.Mock;
import memphis.fridge.server.ioc.MockInjectingRunner.MockManager;
import memphis.fridge.server.ioc.SessionState;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;
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
@MockInjectingRunner.WithModules({AuthModule.class})
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
	MockManager mocks;

	@Inject
	@MockInjectingRunner.Mock
	SessionState s;

	@Before
	public void setUp() {
		mocks.reset();
	}

	@Test(expected = AuthenticationException.class)
	public void testPurchaseNotAuthenticated() throws Exception {
		expect(s.isAuthenticated()).andReturn(false);
		test();
	}

	@Test(expected = FridgeException.class)
	public void testPurchaseErrorGettingDiscount() throws Exception {
		expect(s.isAuthenticated()).andReturn(true);
		expect(s.getUser()).andReturn(user);
		expect(user.isGrad()).andReturn(false);
		expect(p.fridge.getGraduateDiscount()).andThrow(new FridgeException("Database error"));
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

		Messages.TransactionResponse response = test();

		assertEquals(toCents(balance), response.getBalance());
		assertEquals(toCents(cost), response.getCost());
	}

	@Test
	public void testPurchaseUndergrad() throws Exception {
		final BigDecimal balance = fromCents(1000);
		final BigDecimal cost = orderTotal(false);
		expectUserInit(false);
		expectProductUpdates(false);
		expectUserUpdates(cost);
		expectResponse(cost, balance);

		Messages.TransactionResponse response = test();

		assertEquals(toCents(balance), response.getBalance());
		assertEquals(toCents(cost), response.getCost());
	}

	/* Helper methods */

	private Messages.TransactionResponse test() {
		Messages.TransactionResponse r;
		mocks.replay();

		try {
			r = p.purchase(order());
			assertNotNull(r);
		} catch (FridgeException ex) {
			mocks.verify();
			throw ex;
		}
		mocks.verify();

		return r;
	}

	private void expectUserInit(boolean isGrad) {
		expect(s.isAuthenticated()).andReturn(true);
		expect(s.getUser()).andReturn(user);
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
		expect(user.getUsername()).andReturn(USERNAME);
		expect(user.getBalance()).andReturn(BigDecimal.TEN);
	}
}
