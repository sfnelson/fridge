package memphis.fridge.server.services;

import javax.inject.Inject;
import memphis.fridge.domain.Product;
import memphis.fridge.domain.User;
import memphis.fridge.protocol.Messages;
import memphis.fridge.server.ioc.AuthModule;
import memphis.fridge.server.ioc.SessionState;
import memphis.fridge.test.*;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static memphis.fridge.server.TestingData.*;
import static memphis.fridge.utils.CurrencyUtils.toCents;
import static org.mockito.Mockito.when;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 7/10/12
 */
@RunWith(GuiceTestRunner.class)
@TestModule(AuthModule.class)
public class GetProductsTest {

	@Inject
	@ClassRule
	public static GuiceMockitoProvider mocks;

	@Inject
	@InjectMocks
	GetProducts p;

	@Inject
	@Mock
	User u;

	@Mock
	SessionState s;

	@Test
	public void testGetProductsNoUser() throws Exception {
		when(p.fridge.getGraduateDiscount()).thenReturn(GRAD_TAX);
		when(p.products.getEnabledProducts()).thenReturn(products());

		Messages.StockResponse r = p.getProducts(null);

		assertNotNull(r);
		assertEquals(2, r.getStockCount());
		checkCoke(r.getStock(0), false);
		checkCookie(r.getStock(1), false);
	}

	@Test
	public void testGetProductsGrad() throws Exception {
		when(p.fridge.getGraduateDiscount()).thenReturn(GRAD_TAX);
		when(p.products.getEnabledProducts()).thenReturn(products());
		when(p.users.retrieveUser(USERNAME)).thenReturn(u);
		when(u.isGrad()).thenReturn(true);

		Messages.StockResponse r = p.getProducts(USERNAME);

		assertNotNull(r);
		assertEquals(2, r.getStockCount());
		checkCoke(r.getStock(0), true);
		checkCookie(r.getStock(1), true);
	}

	@Test
	public void testGetProductsUGrad() throws Exception {
		when(p.fridge.getGraduateDiscount()).thenReturn(GRAD_TAX);
		when(p.products.getEnabledProducts()).thenReturn(products());
		when(p.users.retrieveUser(USERNAME)).thenReturn(u);
		when(u.isGrad()).thenReturn(false);

		Messages.StockResponse r = p.getProducts(USERNAME);

		assertNotNull(r);
		assertEquals(2, r.getStockCount());
		checkCoke(r.getStock(0), false);
		checkCookie(r.getStock(1), false);
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
