package memphis.fridge.server.services;

import javax.inject.Inject;

import com.google.common.collect.Lists;
import memphis.fridge.domain.User;
import memphis.fridge.protocol.Messages;
import memphis.fridge.server.ioc.AuthModule;
import memphis.fridge.server.ioc.SessionState;
import memphis.fridge.test.*;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
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
		when(p.products.getEnabledProducts()).thenReturn(Lists.newArrayList(Coke.create(), Cookie.create()));

		Messages.StockResponse r = p.getProducts(null);

		assertNotNull(r);
		assertEquals(2, r.getStockCount());
		checkCoke(r.getStock(0), false);
		checkCookie(r.getStock(1), false);
	}

	@Test
	public void testGetProductsGrad() throws Exception {
		when(p.fridge.getGraduateDiscount()).thenReturn(GRAD_TAX);
		when(p.products.getEnabledProducts()).thenReturn(Lists.newArrayList(Coke.create(), Cookie.create()));
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
		when(p.products.getEnabledProducts()).thenReturn(Lists.newArrayList(Coke.create(), Cookie.create()));
		when(p.users.retrieveUser(USERNAME)).thenReturn(u);
		when(u.isGrad()).thenReturn(false);

		Messages.StockResponse r = p.getProducts(USERNAME);

		assertNotNull(r);
		assertEquals(2, r.getStockCount());
		checkCoke(r.getStock(0), false);
		checkCookie(r.getStock(1), false);
	}

	private void checkCoke(Messages.StockResponse.Stock item, boolean isGrad) {
		assertEquals(Coke.CODE, item.getProductCode());
		assertEquals(Coke.DESC, item.getDescription());
		assertEquals(Coke.STOCK, item.getInStock());
		assertEquals(toCents(Coke.total(isGrad)), item.getPrice());
		assertEquals(Drinks.TITLE, item.getCategory());
		assertEquals(Drinks.ORDER, item.getCategoryOrder());
	}

	private void checkCookie(Messages.StockResponse.Stock item, boolean isGrad) {
        assertEquals(Cookie.CODE, item.getProductCode());
        assertEquals(Cookie.DESC, item.getDescription());
        assertEquals(Cookie.STOCK, item.getInStock());
        assertEquals(toCents(Cookie.total(isGrad)), item.getPrice());
        assertEquals(Snacks.TITLE, item.getCategory());
        assertEquals(Snacks.ORDER, item.getCategoryOrder());
	}
}
