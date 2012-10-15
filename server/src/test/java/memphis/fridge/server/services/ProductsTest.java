package memphis.fridge.server.services;

import javax.inject.Inject;

import memphis.fridge.dao.ProductCategoryDAO;
import memphis.fridge.dao.ProductsDAO;
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

import static memphis.fridge.server.TestingData.*;
import static memphis.fridge.utils.CurrencyUtils.toCents;
import static memphis.fridge.utils.CurrencyUtils.toPercent;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(GuiceTestRunner.class)
@TestModule(AuthModule.class)
public class ProductsTest {

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
    ProductCategoryDAO categoryDAO;

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
	public void testAddProductNotAuthenticated() throws Exception {
        when(s.isAuthenticated()).thenReturn(false);

        Coke.add(products);
	}

	@Test(expected = AccessDeniedException.class)
	public void testAddProductNotAdmin() throws Exception {
        when(s.isAdmin()).thenReturn(false);

        products.addProduct(Coke.CODE, Coke.DESC, Drinks.ID, 0, 0, toCents(Coke.BASE), toPercent(Coke.TAX_RATE));
	}

	@Test(expected = ProductExistsException.class)
	public void testAddProductExistingProduct() throws Exception {
        doThrow(new ProductExistsException(Coke.create()))
                .when(productsDAO).add(any(Product.class));
        Coke.add(products);
	}

	@Test(expected = InvalidCategoryException.class)
	public void testAddProductInvalidCategory() throws Exception {
        doThrow(new InvalidCategoryException(Drinks.ID)).when(categoryDAO).findCategory(Drinks.ID);
        try {
            Coke.add(products);
        }
        finally {
            verify(productsDAO).checkProductNotExists(Coke.CODE);
        }
	}

	@Test
	public void testAddProduct() throws Exception {
		when(products.categoryDAO.findCategory(Drinks.ID)).thenReturn(Drinks.create());

        Coke.add(products);

        verify(productsDAO).add(argThat(samePropertyValuesAs(Coke.create())));
	}

	@Test(expected = AuthenticationException.class)
	public void testUpdateProductInvalidHMac() throws Exception {
		when(s.isAuthenticated()).thenReturn(false);

        Cookie.update(products, false);
	}

	@Test(expected = AccessDeniedException.class)
	public void testUpdateProductNotAdmin() throws Exception {
		when(s.isAdmin()).thenReturn(false);

        Cookie.update(products, false);
	}

	@Test(expected = InvalidProductException.class)
	public void testUpdateProductNotFound() throws Exception {
        doThrow(new InvalidProductException(Cookie.CODE))
                .when(productsDAO).findProduct(Cookie.CODE);

        Cookie.update(products, false);
	}

	@Test(expected = InvalidCategoryException.class)
	public void testUpdateProductInvalidCategory() throws Exception {
		when(products.productsDAO.findProduct(Cookie.CODE))
                .thenReturn(Cookie.create());

        doThrow(new InvalidCategoryException(Snacks.ID))
                .when(products.categoryDAO).findCategory(Chocolate.ID);

        products.updateProduct(Cookie.CODE, Cookie.DESC, Chocolate.ID, 0, toCents(Cookie.BASE),
                toPercent(Cookie.TAX_RATE), false);
	}

	@Test
	public void testUpdateProduct() throws Exception {
		when(products.productsDAO.findProduct(Cookie.CODE)).thenReturn(Cookie.create());
		when(products.categoryDAO.findCategory(Snacks.ID)).thenReturn(Snacks.create());

		Product newProduct = Cookie.create();
		newProduct.setEnabled(false);

        Cookie.update(products, false);

        ArgumentCaptor<Product> p = ArgumentCaptor.forClass(Product.class);
        verify(productsDAO).save(p.capture());
        assertThat(p.getValue(), samePropertyValuesAs(newProduct));
	}
}
