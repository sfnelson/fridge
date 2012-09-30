package memphis.fridge.server.services;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import com.google.common.collect.Lists;
import memphis.fridge.dao.ProductsDAO;
import memphis.fridge.dao.UserDAO;
import memphis.fridge.domain.Product;
import memphis.fridge.exceptions.FridgeException;
import memphis.fridge.utils.CurrencyUtils;
import memphis.fridge.utils.Pair;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.easymock.EasyMock.*;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 30/09/12
 */
public class PurchaseTest {
	String snonce;
	String user;
	String toUser;
	List<Pair<String, Integer>> items;
	String hmac;
	Product coke;
	Product cookie;
	BigDecimal cost;

	Purchase purchase;
	UserDAO users;
	ProductsDAO products;

	@Before
	@SuppressWarnings("unchecked")
	public void setUp() {
		snonce = "SNONCE";
		user = "USER";
		items = Lists.newArrayList(
				new Pair<String, Integer>("CC", 2),
				new Pair<String, Integer>("CT", 1)
		);
		hmac = "HMAC";
		cost = new BigDecimal(BigInteger.valueOf(2 * 120 + 1 * 240), 2);
		coke = new Product("CC", "Coke Can", CurrencyUtils.fromCents(100), CurrencyUtils.fromPercent(20), null);
		cookie = new Product("CT", "Cookie Time", CurrencyUtils.fromCents(200), CurrencyUtils.fromPercent(20), null);

		purchase = new Purchase();
		users = EasyMock.createMock(UserDAO.class);
		purchase.users = users;
		products = EasyMock.createMock(ProductsDAO.class);
		purchase.products = products;

		reset(users, products);
	}

	@After
	public void tearDown() {
		verify(users, products);
	}

	@Test(expected = FridgeException.class)
	public void testPurchaseBadHMAC() throws Exception {
		users.validateHMAC(user, hmac, snonce, user, items);
		expectLastCall().andThrow(new FridgeException(1, "invalid hmac"));

		replay(users, products);

		purchase.purchase(snonce, user, items, hmac);
	}

	@Test(expected = FridgeException.class)
	public void testPurchaseInvalidProduct() throws Exception {
		users.validateHMAC(user, hmac, snonce, user, items);

		expect(products.findProduct("CC")).andReturn(coke);
		expect(products.findProduct("CT")).andReturn(cookie);
		users.checkSufficientBalance(user, cost);

		replay(users, products);

		purchase.purchase(snonce, user, items, hmac);
	}
}
