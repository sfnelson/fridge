package memphis.fridge.server.services;

import javax.inject.Inject;

import memphis.fridge.dao.ProductsDAO;
import memphis.fridge.test.data.Chocolate;
import memphis.fridge.test.data.Cookie;
import memphis.fridge.domain.Product;
import memphis.fridge.domain.User;
import memphis.fridge.exceptions.*;
import memphis.fridge.server.ioc.AuthModule;
import memphis.fridge.server.ioc.SessionState;
import memphis.fridge.test.*;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;

import static memphis.fridge.test.data.Utils.*;
import static memphis.fridge.utils.CurrencyUtils.toCents;
import static memphis.fridge.utils.CurrencyUtils.toPercent;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(GuiceTestRunner.class)
@TestModule(AuthModule.class)
public class ProductsUpdateTest {

    @Inject
    @ClassRule
    public static GuiceMockitoProvider mocks;

	@Inject
    @InjectMocks
	Products products;

    @Inject
    @Mock
    ProductsDAO productsDAO;

	@Inject
	@Mock
	SessionState s;

	@Inject
	@Mock
	User user;

    @Before
    public void setUp() {
        mocks.reset();
        when(s.isAuthenticated()).thenReturn(true);
        when(s.isAdmin()).thenReturn(true);
        when(s.getUser()).thenReturn(user);
        when(user.getUsername()).thenReturn(USERNAME);
    }

	@Test(expected = AuthenticationException.class)
	public void testUpdateProductInvalidHMac() throws Exception {
		when(s.isAuthenticated()).thenReturn(false);

        updateCookie(products, Chocolate.ID, false);
	}

	@Test(expected = AccessDeniedException.class)
	public void testUpdateProductNotAdmin() throws Exception {
		when(s.isAdmin()).thenReturn(false);

        updateCookie(products, Chocolate.ID, false);
	}

	@Test(expected = InvalidProductException.class)
	public void testUpdateProductNotFound() throws Exception {
        doThrow(new InvalidProductException(Cookie.CODE))
                .when(productsDAO).findProduct(Cookie.CODE);

        updateCookie(products, Chocolate.ID, false);
	}

	@Test(expected = InvalidCategoryException.class)
	public void testUpdateProductInvalidCategory() throws Exception {
		when(products.productsDAO.findProduct(Cookie.CODE))
                .thenReturn(Cookie.create());
        doThrow(new InvalidCategoryException(Chocolate.ID))
                .when(products.categoryDAO).findCategory(Chocolate.ID);

        updateCookie(products, Chocolate.ID, false);
	}

	@Test
	public void testUpdateProduct() throws Exception {
		when(products.productsDAO.findProduct(Cookie.CODE)).thenReturn(Cookie.create());
		when(products.categoryDAO.findCategory(Chocolate.ID)).thenReturn(Chocolate.create());

		Product newProduct = Cookie.create();
		newProduct.setEnabled(false);
        newProduct.setCategory(Chocolate.create());

        updateCookie(products, Chocolate.ID, false);

        ArgumentCaptor<Product> p = ArgumentCaptor.forClass(Product.class);
        verify(productsDAO).save(p.capture());
        assertThat(p.getValue(), samePropertyValuesAs(newProduct));
	}


    public static void updateCookie(Products service, int category, boolean enabled) {
        service.updateProduct(Cookie.CODE, Cookie.DESC, category, 0, toCents(Cookie.BASE), toPercent(Cookie.TAX_RATE),
                enabled);
    }
}
