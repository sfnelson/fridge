package memphis.fridge.server.services;

import java.math.BigDecimal;
import java.util.List;

import com.google.common.collect.Lists;
import javax.inject.Inject;
import memphis.fridge.domain.Product;
import memphis.fridge.domain.User;
import memphis.fridge.exceptions.*;
import memphis.fridge.server.io.Response;
import memphis.fridge.server.io.ResponseSerializer;
import memphis.fridge.server.ioc.MockInjectingRunner;
import memphis.fridge.server.ioc.MockInjectingRunner.Mock;
import memphis.fridge.server.ioc.MockInjectingRunner.MockManager;
import memphis.fridge.utils.CurrencyUtils;
import memphis.fridge.utils.Pair;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertNotNull;
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

	String snonce;
	String username;
	List<Pair<String, Integer>> items;
	String hmac;
	Product coke;
	Product cookie;
	BigDecimal costGrad;
	BigDecimal costUndergrad;
	BigDecimal gradDiscount;

	@Inject
	Purchase p;

	@Inject
	@Mock
	User user;

	@Inject
	@Mock
	ResponseSerializer response;

	@Inject
	MockManager mocks;

	@Before
	@SuppressWarnings("unchecked")
	public void setUp() {
		snonce = "SNONCE";
		username = "USER";
		items = Lists.newArrayList(
				new Pair<String, Integer>("CC", 2),
				new Pair<String, Integer>("CT", 1)
		);
		hmac = "HMAC";
		costGrad = BigDecimal.valueOf(2 * 120 + 1 * 240, 2);
		costUndergrad = BigDecimal.valueOf(2 * 130 + 1 * 260, 2);
		gradDiscount = BigDecimal.valueOf(100, 1);
		coke = new Product("CC", "Coke Can", CurrencyUtils.fromCents(100), CurrencyUtils.fromPercent(20), null);
		coke.setInStock(5);
		cookie = new Product("CT", "Cookie Time", CurrencyUtils.fromCents(200), CurrencyUtils.fromPercent(20), null);
		cookie.setInStock(5);

		mocks.reset();
	}

	@Test(expected = FridgeException.class)
	public void testPurchaseBadHMAC() throws Exception {
		p.users.validateHMAC(username, hmac, snonce, username, items);
		expectLastCall().andThrow(new FridgeException(1, "invalid hmac"));
		test();
	}

	@Test(expected = InvalidUserException.class)
	public void testPurchaseErrorGettingUser() throws Exception {
		p.users.validateHMAC(username, hmac, snonce, username, items);
		expect(p.users.retrieveUser(username)).andThrow(new InvalidUserException(username));
		test();
	}

	@Test(expected = FridgeException.class)
	public void testPurchaseErrorGettingDiscount() throws Exception {
		p.users.validateHMAC(username, hmac, snonce, username, items);
		expect(p.users.retrieveUser(username)).andReturn(user);
		expect(user.isGrad()).andReturn(false);
		expect(p.fridge.getGraduateDiscount()).andThrow(new FridgeException(1, "Database error"));
		test();
	}

	@Test(expected = InvalidProductException.class)
	public void testPurchaseInvalidProduct() throws Exception {
		expectUserInteractions(true);
		expect(p.products.findProduct("CC")).andThrow(new InvalidProductException());
		test();
	}

	@Test(expected = InsufficientStockException.class)
	public void testPurchaseInsufficientStock() throws Exception {
		expectUserInteractions(true);
		expect(p.products.findProduct("CC")).andReturn(coke);
		coke.setInStock(1);
		test();
	}

	@Test(expected = InvalidProductException.class)
	public void testPurchaseInvalidProductTwo() throws Exception {
		expectUserInteractions(true);
		expect(p.products.findProduct("CC")).andReturn(coke);
		p.products.removeProduct(coke, 2);
		p.purchases.createPurchase(user, coke, 2, BigDecimal.valueOf(2 * 100, 2), BigDecimal.valueOf(2 * 20, 2));
		expect(p.products.findProduct("CT")).andThrow(new InvalidProductException());
		test();
	}

	@Test(expected = InsufficientFundsException.class)
	public void testPurchaseInsufficientFundsUndergrad() throws Exception {
		expectUserInteractions(false);
		expectProductInteractions(false);
		p.users.removeFunds(user, costUndergrad);
		expectLastCall().andThrow(new InsufficientFundsException(username, BigDecimal.ZERO));
		test();
	}

	@Test(expected = InsufficientFundsException.class)
	public void testPurchaseInsufficientFundsGrad() throws Exception {
		expectUserInteractions(true);
		expectProductInteractions(true);
		p.users.removeFunds(user, costGrad);
		expectLastCall().andThrow(new InsufficientFundsException(username, BigDecimal.ZERO));
		test();
	}

	@Test
	public void testPurchaseGrad() throws Exception {
		expectUserInteractions(true);
		expectProductInteractions(true);
		p.users.removeFunds(user, costGrad);
		p.creditLog.createPurchase(user, costGrad);
		expect(p.users.retrieveUser(username)).andReturn(user);
		expect(user.getBalance()).andReturn(BigDecimal.TEN);
		expect(p.users.createHMAC(username, snonce, 1000, toCents(costGrad))).andReturn(hmac);
		response.visitInteger("balance", 1000);
		response.visitInteger("order_total", toCents(costGrad));
		response.visitString("hmac", hmac);
		test();
	}

	@Test
	public void testPurchaseUndergrad() throws Exception {
		expectUserInteractions(false);
		expectProductInteractions(false);
		p.users.removeFunds(user, costUndergrad);
		p.creditLog.createPurchase(user, costUndergrad);
		expect(p.users.retrieveUser(username)).andReturn(user);
		expect(user.getBalance()).andReturn(BigDecimal.TEN);
		expect(p.users.createHMAC(username, snonce, 1000, toCents(costUndergrad))).andReturn(hmac);
		response.visitInteger("balance", 1000);
		response.visitInteger("order_total", toCents(costUndergrad));
		response.visitString("hmac", hmac);
		test();
	}

	/* Helper methods */

	private void test() {
		mocks.replay();

		try {
			Response r = p.purchase(snonce, username, items, hmac);
			assertNotNull(r);
			r.visitResponse(response);
		} catch (FridgeException ex) {
			mocks.verify();
			throw ex;
		}
		mocks.verify();
	}

	private void expectUserInteractions(boolean isGrad) {
		p.users.validateHMAC(username, hmac, snonce, username, items);
		expect(p.users.retrieveUser(username)).andReturn(user);
		expect(user.isGrad()).andReturn(isGrad);
		if (!isGrad) {
			expect(p.fridge.getGraduateDiscount()).andReturn(gradDiscount);
		}
	}

	private void expectProductInteractions(boolean isGrad) {
		expect(p.products.findProduct("CC")).andReturn(coke);
		p.products.removeProduct(coke, 2);
		if (isGrad) {
			p.purchases.createPurchase(user, coke, 2, BigDecimal.valueOf(2 * 100, 2), BigDecimal.valueOf(2 * 20, 2));
		} else {
			p.purchases.createPurchase(user, coke, 2, BigDecimal.valueOf(2 * 100, 2), BigDecimal.valueOf(2 * 30, 2));
		}
		expect(p.products.findProduct("CT")).andReturn(cookie);
		p.products.removeProduct(cookie, 1);
		if (isGrad) {
			p.purchases.createPurchase(user, cookie, 1, BigDecimal.valueOf(200, 2), BigDecimal.valueOf(40, 2));
		} else {
			p.purchases.createPurchase(user, cookie, 1, BigDecimal.valueOf(200, 2), BigDecimal.valueOf(60, 2));
		}
	}
}
