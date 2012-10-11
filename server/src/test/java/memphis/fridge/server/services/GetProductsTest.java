package memphis.fridge.server.services;

import javax.inject.Inject;
import memphis.fridge.domain.Product;
import memphis.fridge.domain.User;
import memphis.fridge.exceptions.FridgeException;
import memphis.fridge.protocol.Messages;
import memphis.fridge.server.ioc.AuthModule;
import memphis.fridge.server.ioc.MockInjectingRunner;
import memphis.fridge.server.ioc.SessionState;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static memphis.fridge.server.TestingData.*;
import static memphis.fridge.utils.CurrencyUtils.toCents;
import static org.easymock.EasyMock.expect;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 7/10/12
 */
@RunWith(MockInjectingRunner.class)
@MockInjectingRunner.ToInject({GetProducts.class})
@MockInjectingRunner.WithModules({AuthModule.class})
public class GetProductsTest {

	@Inject
	GetProducts p;

	@Inject
	MockInjectingRunner.MockManager m;

	@Inject
	@MockInjectingRunner.Mock
	User u;

	@MockInjectingRunner.Mock
	SessionState s;

	@Before
	public void setUp() throws Exception {
		m.reset();
	}

	@Test
	public void testGetProductsNoUser() throws Exception {
		expect(p.fridge.getGraduateDiscount()).andReturn(GRAD_TAX);
		expect(p.products.getEnabledProducts()).andReturn(products());

		Messages.StockResponse r = test(null);

		assertEquals(2, r.getStockCount());
		checkCoke(r.getStock(0), false);
		checkCookie(r.getStock(1), false);
	}

	@Test
	public void testGetProductsGrad() throws Exception {
		expect(p.fridge.getGraduateDiscount()).andReturn(GRAD_TAX);
		expect(p.products.getEnabledProducts()).andReturn(products());
		expect(p.users.retrieveUser(USERNAME)).andReturn(u);
		expect(u.isGrad()).andReturn(true);

		Messages.StockResponse r = test(USERNAME);

		assertEquals(2, r.getStockCount());
		checkCoke(r.getStock(0), true);
		checkCookie(r.getStock(1), true);
	}

	@Test
	public void testGetProductsUGrad() throws Exception {
		expect(p.fridge.getGraduateDiscount()).andReturn(GRAD_TAX);
		expect(p.products.getEnabledProducts()).andReturn(products());
		expect(p.users.retrieveUser(USERNAME)).andReturn(u);
		expect(u.isGrad()).andReturn(false);

		Messages.StockResponse r = test(USERNAME);

		assertEquals(2, r.getStockCount());
		checkCoke(r.getStock(0), false);
		checkCookie(r.getStock(1), false);
	}

	private Messages.StockResponse test(String username) {
		Messages.StockResponse r;
		m.replay();
		try {
			r = p.getProducts(username);
			assertNotNull(r);
		} catch (FridgeException ex) {
			m.verify();
			throw ex;
		}
		m.verify();
		return r;
	}

	private void checkCoke(Messages.StockResponse.Stock item, boolean isGrad) {
		Product coke = coke();
		assertEquals(coke.getProductCode(), item.getProductCode());
		assertEquals(coke.getDescription(), item.getDescription());
		assertEquals(coke.getInStock(), item.getInStock());
		assertEquals(toCents(cokeTotal(isGrad)), item.getPrice());
		assertEquals(coke.getCategory().getTitle(), item.getCategory());
		assertEquals(coke.getCategory().getDisplaySequence(), item.getCategoryOrder());
	}

	private void checkCookie(Messages.StockResponse.Stock item, boolean isGrad) {
		Product cookie = cookie();
		assertEquals(cookie.getProductCode(), item.getProductCode());
		assertEquals(cookie.getDescription(), item.getDescription());
		assertEquals(cookie.getInStock(), item.getInStock());
		assertEquals(toCents(cookieTotal(isGrad)), item.getPrice());
		assertEquals(cookie.getCategory().getTitle(), item.getCategory());
		assertEquals(cookie.getCategory().getDisplaySequence(), item.getCategoryOrder());
	}
}
