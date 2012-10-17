package memphis.fridge.server.services;

import javax.inject.Inject;

import com.google.common.collect.Lists;
import memphis.fridge.test.data.Coke;
import memphis.fridge.test.data.Cookie;
import memphis.fridge.test.data.Drinks;
import memphis.fridge.test.data.Snacks;
import memphis.fridge.domain.User;
import memphis.fridge.protocol.Messages;
import memphis.fridge.server.ioc.AuthModule;
import memphis.fridge.server.ioc.SessionState;
import memphis.fridge.test.*;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static memphis.fridge.test.data.Utils.*;
import static memphis.fridge.utils.CurrencyUtils.toCents;
import static org.mockito.Mockito.when;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 7/10/12
 */
@RunWith(GuiceTestRunner.class)
@TestModule(AuthModule.class)
public class ProductsListTest {

	@Inject
	@ClassRule
	public static GuiceMockitoProvider mocks;

	@Inject
	@InjectMocks
	Products p;

	@Inject
	@Mock
	User u;

	@Mock
	SessionState s;

    @Before
    public void setUp() {
        when(p.session.isAuthenticated()).thenReturn(true);
        when(p.session.getUser()).thenReturn(u);
        when(u.isGrad()).thenReturn(true);
        when(p.fridgeDAO.getGraduateDiscount()).thenReturn(GRAD_TAX);
        when(p.productsDAO.getEnabledProducts()).thenReturn(
                Lists.newArrayList(Coke.create(), Cookie.create()));
    }


	@Test
	public void testGetProductsNoUser() throws Exception {
        when(p.session.isAuthenticated()).thenReturn(false); // not actually called, just for safety.

		Messages.StockResponse r = p.getProducts();

		assertNotNull(r);
		assertEquals(2, r.getStockCount());
		checkCoke(r.getStock(0), false);
		checkCookie(r.getStock(1), false);
	}

	@Test
	public void testGetProductsGrad() throws Exception {
		Messages.StockResponse r = p.getProductsAuthenticated();

		assertNotNull(r);
		assertEquals(2, r.getStockCount());
		checkCoke(r.getStock(0), true);
		checkCookie(r.getStock(1), true);
	}

	@Test
	public void testGetProductsUGrad() throws Exception {
        when(u.isGrad()).thenReturn(false);

		Messages.StockResponse r = p.getProductsAuthenticated();

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
