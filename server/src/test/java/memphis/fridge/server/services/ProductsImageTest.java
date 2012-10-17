package memphis.fridge.server.services;

import memphis.fridge.test.data.Coke;
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

import javax.inject.Inject;

import static memphis.fridge.test.data.Utils.USERNAME;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GuiceTestRunner.class)
@TestModule(AuthModule.class)
public class ProductsImageTest {

    @Inject
    @ClassRule
    public static GuiceMockitoProvider mocks;

    @Inject
    @InjectMocks
    Products products;

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

    @Test(expected = InvalidProductException.class)
    public void testGetProductImageInvalidProduct() throws Exception {
        when(products.productsDAO.findProduct(Coke.CODE))
                .thenThrow(new InvalidProductException(Coke.CODE));

        products.getProductImage(Coke.CODE);
    }

    @Test
    public void testGetProductImage() throws Exception {
        Product coke = Coke.create();
        byte[] image = new byte[0];

        when(products.productsDAO.findProduct(Coke.CODE)).thenReturn(coke);
        when(products.productsDAO.getImage(coke)).thenReturn(image);

        assertSame(image, products.getProductImage(Coke.CODE));
    }

    @Test(expected = AuthenticationException.class)
    public void testStoreProductImageNoAuth() throws Exception {
        when(s.isAuthenticated()).thenReturn(false);

        products.storeProductImage(Coke.CODE, new byte[0]);
    }

    @Test(expected = AccessDeniedException.class)
    public void testStoreProductImageNoAdmin() throws Exception {
        when(s.isAdmin()).thenReturn(false);

        products.storeProductImage(Coke.CODE, new byte[0]);
    }

    @Test(expected = InvalidProductException.class)
    public void testStoreProductImageInvalidProduct() throws Exception {
        when(products.productsDAO.findProduct(Coke.CODE))
                .thenThrow(new InvalidProductException(Coke.CODE));

        products.storeProductImage(Coke.CODE, new byte[0]);
    }

    @Test
    public void testStoreProductImage() throws Exception {
        Product coke = Coke.create();
        byte[] image = new byte[0];

        when(products.productsDAO.findProduct(Coke.CODE)).thenReturn(coke);

        products.storeProductImage(Coke.CODE, image);

        verify(products.productsDAO).storeImage(coke, image);
    }

}