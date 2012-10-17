package memphis.fridge.server.services;

import memphis.fridge.dao.ProductCategoryDAO;
import memphis.fridge.dao.ProductsDAO;
import memphis.fridge.test.data.*;
import memphis.fridge.domain.*;
import memphis.fridge.exceptions.*;
import memphis.fridge.server.ioc.AuthModule;
import memphis.fridge.server.ioc.SessionState;
import memphis.fridge.test.*;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.internal.matchers.CapturesArguments;
import org.mockito.internal.matchers.CapturingMatcher;

import javax.inject.Inject;

import static memphis.fridge.utils.CurrencyUtils.toCents;
import static memphis.fridge.utils.CurrencyUtils.toPercent;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GuiceTestRunner.class)
@TestModule(AuthModule.class)
public class ProductsAddTest {

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
        when(s.getUser()).thenReturn(Admin.create());
    }

    @Test(expected = AuthenticationException.class)
    public void testAddProductNotAuthenticated() throws Exception {
        when(s.isAuthenticated()).thenReturn(false);

        addCoke(products);
    }

    @Test(expected = AccessDeniedException.class)
    public void testAddProductNotAdmin() throws Exception {
        when(s.isAdmin()).thenReturn(false);

        addCoke(products);
    }

    @Test(expected = ProductExistsException.class)
    public void testAddProductExistingProduct() throws Exception {
        doThrow(new ProductExistsException(Coke.create()))
                .when(productsDAO).add(any(Product.class));
        addCoke(products);
    }

    @Test(expected = InvalidCategoryException.class)
    public void testAddProductInvalidCategory() throws Exception {
        doThrow(new InvalidCategoryException(Drinks.ID)).when(categoryDAO).findCategory(Drinks.ID);
        try {
            addCoke(products);
        }
        finally {
            verify(productsDAO).checkProductNotExists(Coke.CODE);
        }
    }

    @Test
    public void testAddProduct() throws Exception {
        when(products.categoryDAO.findCategory(Drinks.ID)).thenReturn(Drinks.create());

        addCoke(products);

        ArgumentCaptor<Product> p = ArgumentCaptor.forClass(Product.class);
        verify(productsDAO).add(p.capture());
        assertThat(p.getValue(), samePropertyValuesAs(Coke.create()));
    }

    public static void addCoke(Products service) {
        service.addProduct(Coke.CODE, Coke.DESC, Drinks.ID, Coke.STOCK, 0, toCents(Coke.BASE), toPercent(Coke.TAX_RATE));
    }
}
