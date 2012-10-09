package memphis.fridge.server.services;

import memphis.fridge.domain.Product;
import memphis.fridge.exceptions.*;
import memphis.fridge.server.ioc.MockInjectingRunner;
import memphis.fridge.utils.CurrencyUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

import static memphis.fridge.server.TestingData.*;
import static org.easymock.EasyMock.*;

@RunWith(MockInjectingRunner.class)
@MockInjectingRunner.ToInject(Products.class)
public class ProductsTest {

    private static final Product toAdd = coke();
    private static final Product toUpdate = cookie();

    private static final Object[] ADD_HMAC_DATA = { SNONCE, toAdd.getProductCode(), toAdd.getDescription(),
            toAdd.getCategory().getId(), toAdd.getInStock(), toAdd.getStockLowMark(),
            CurrencyUtils.toCents(toAdd.getCost()), CurrencyUtils.toPercent(toAdd.getMarkup())};

    private static final Object[] UPDATE_HMAC_DATA = { SNONCE, toUpdate.getProductCode(), toUpdate.getDescription(),
            chocolateCategory().getId(), toUpdate.getStockLowMark(), CurrencyUtils.toCents(toUpdate.getCost()),
            CurrencyUtils.toPercent(toUpdate.getMarkup()), false
    };

    @Inject
    private Products products;

    @Inject
    private MockInjectingRunner.MockManager mockManager;

    @Before
    public void setUp() {
        mockManager.reset();
    }

    @Test(expected = FridgeException.class)
    public void testAddProductInvalidHMAC() throws Exception {
        products.userDAO.validateHMAC(USERNAME, HMAC, ADD_HMAC_DATA);
        expectLastCall().andThrow(new FridgeException(1, "Invalid HMAC"));

        testAdd();
    }

    @Test(expected = AccessDeniedException.class)
    public void testAddProductNotAdmin() throws Exception {
        products.userDAO.validateHMAC(USERNAME, HMAC, ADD_HMAC_DATA);
        products.userDAO.checkAdmin(USERNAME);
        expectLastCall().andThrow(new AccessDeniedException(USERNAME));

        testAdd();
    }

    @Test(expected = ProductExistsException.class)
    public void testAddProductExistingProduct() throws Exception {
        products.userDAO.validateHMAC(USERNAME, HMAC, ADD_HMAC_DATA);
        products.userDAO.checkAdmin(USERNAME);
        products.productsDAO.checkProductNotExists(toAdd.getProductCode());
        expectLastCall().andThrow(new ProductExistsException(coke()));

        testAdd();
    }

    @Test(expected = InvalidCategoryException.class)
    public void testAddProductInvalidCategory() throws Exception {
        products.userDAO.validateHMAC(USERNAME, HMAC, ADD_HMAC_DATA);
        products.userDAO.checkAdmin(USERNAME);
        products.productsDAO.checkProductNotExists(toAdd.getProductCode());
        int categoryId = toAdd.getCategory().getId();
        expect(products.categoryDAO.findCategory(categoryId)).andThrow(new InvalidCategoryException(categoryId));

        testAdd();
    }

    @Test
    public void testAddProduct() throws Exception {
        products.userDAO.validateHMAC(USERNAME, HMAC, ADD_HMAC_DATA);
        products.userDAO.checkAdmin(USERNAME);
        products.productsDAO.checkProductNotExists(toAdd.getProductCode());
        expect(products.categoryDAO.findCategory(toAdd.getCategory().getId())).andReturn(toAdd.getCategory());
        products.productsDAO.add(toAdd);

        testAdd();
    }

    private void testAdd() {
        mockManager.replay();

        try {
            products.addProduct(SNONCE, USERNAME, toAdd.getProductCode(), toAdd.getDescription(),
                    toAdd.getCategory().getId(), toAdd.getInStock(), toAdd.getStockLowMark(),
                    CurrencyUtils.toCents(toAdd.getCost()), CurrencyUtils.toPercent(toAdd.getMarkup()), HMAC);
            mockManager.verify();
        } catch (FridgeException e) {
            mockManager.verify();
            throw e;
        }
    }

    @Test(expected = FridgeException.class)
    public void testUpdateProductInvalidHMac() throws Exception {
        products.userDAO.validateHMAC(USERNAME, HMAC, UPDATE_HMAC_DATA);
        expectLastCall().andThrow(new FridgeException(1, "Invalid HMAC"));

        testUpdate();
    }

    @Test(expected = AccessDeniedException.class)
    public void testUpdateProductNotAdmin() throws Exception {
        products.userDAO.validateHMAC(USERNAME, HMAC, UPDATE_HMAC_DATA);
        products.userDAO.checkAdmin(USERNAME);
        expectLastCall().andThrow(new AccessDeniedException(USERNAME));

        testUpdate();
    }

    @Test(expected = InvalidProductException.class)
    public void testUpdateProductNotFound() throws Exception {
        products.userDAO.validateHMAC(USERNAME, HMAC, UPDATE_HMAC_DATA);
        products.userDAO.checkAdmin(USERNAME);
        expect(products.productsDAO.findProduct(toUpdate.getProductCode())).andThrow(new InvalidProductException(toUpdate.getProductCode()));

        testUpdate();
    }

    @Test(expected = InvalidCategoryException.class)
    public void testUpdateProductInvalidCategory() throws Exception {
        products.userDAO.validateHMAC(USERNAME, HMAC, UPDATE_HMAC_DATA);
        products.userDAO.checkAdmin(USERNAME);
        expect(products.productsDAO.findProduct(toUpdate.getProductCode())).andReturn(cookie());
        expect(products.categoryDAO.findCategory(chocolateCategory().getId())).andThrow(new InvalidCategoryException(chocolateCategory().getId()));

        testUpdate();
    }

    @Test
    public void testUpdateProduct() throws Exception {
        products.userDAO.validateHMAC(USERNAME, HMAC, UPDATE_HMAC_DATA);
        products.userDAO.checkAdmin(USERNAME);
        expect(products.productsDAO.findProduct(toUpdate.getProductCode())).andReturn(cookie());
        expect(products.categoryDAO.findCategory(chocolateCategory().getId())).andReturn(chocolateCategory());

        Product newProduct = new Product(toUpdate.getProductCode(), toUpdate.getDescription(), toUpdate.getCost(),
                toUpdate.getMarkup(), toUpdate.getInStock(), toUpdate.getStockLowMark(), chocolateCategory());
        newProduct.setEnabled(false);

        products.productsDAO.save(newProduct);

        testUpdate();
    }

    private void testUpdate() {
        mockManager.replay();

        try {
            products.updateProduct(SNONCE, USERNAME, toUpdate.getProductCode(), toUpdate.getDescription(),
                    chocolateCategory().getId(), toUpdate.getStockLowMark(),
                    CurrencyUtils.toCents(toUpdate.getCost()), CurrencyUtils.toPercent(toUpdate.getMarkup()), false, HMAC);
            mockManager.verify();
        } catch (FridgeException e) {
            mockManager.verify();
            throw e;
        }
    }
}
