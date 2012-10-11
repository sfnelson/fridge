package memphis.fridge.server.services;

import javax.inject.Inject;
import memphis.fridge.domain.Product;
import memphis.fridge.domain.User;
import memphis.fridge.exceptions.*;
import memphis.fridge.server.ioc.AuthModule;
import memphis.fridge.server.ioc.MockInjectingRunner;
import memphis.fridge.server.ioc.SessionState;
import memphis.fridge.utils.CurrencyUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static memphis.fridge.server.TestingData.*;
import static memphis.fridge.utils.CurrencyUtils.toCents;
import static memphis.fridge.utils.CurrencyUtils.toPercent;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;

@RunWith(MockInjectingRunner.class)
@MockInjectingRunner.ToInject(Products.class)
@MockInjectingRunner.WithModules({AuthModule.class})
public class ProductsTest {

	private static final Product toAdd = coke();
	private static final Product toUpdate = cookie();

	private static final Object[] ADD_HMAC_DATA = {SNONCE, toAdd.getProductCode(), toAdd.getDescription(),
			toAdd.getCategory().getId(), toAdd.getInStock(), toAdd.getStockLowMark(),
			CurrencyUtils.toCents(toAdd.getCost()), CurrencyUtils.toPercent(toAdd.getMarkup())};

	private static final Object[] UPDATE_HMAC_DATA = {SNONCE, toUpdate.getProductCode(), toUpdate.getDescription(),
			chocolateCategory().getId(), toUpdate.getStockLowMark(), CurrencyUtils.toCents(toUpdate.getCost()),
			CurrencyUtils.toPercent(toUpdate.getMarkup()), false
	};

	@Inject
	private Products products;

	@Inject
	private MockInjectingRunner.MockManager mockManager;

	@Inject
	@MockInjectingRunner.Mock
	SessionState s;

	@Inject
	@MockInjectingRunner.Mock
	User u;

	@Before
	public void setUp() {
		mockManager.reset();
	}

	@Test(expected = AuthenticationException.class)
	public void testAddProductNotAuthenticated() throws Exception {
		expect(s.isAuthenticated()).andReturn(false);

		testAdd();
	}

	@Test(expected = AccessDeniedException.class)
	public void testAddProductNotAdmin() throws Exception {
		expect(s.isAuthenticated()).andReturn(true);
		expect(s.isAdmin()).andReturn(false);
		expect(s.getUser()).andReturn(u);
		expect(u.getUsername()).andReturn(USERNAME);

		testAdd();
	}

	@Test(expected = ProductExistsException.class)
	public void testAddProductExistingProduct() throws Exception {
		expect(s.isAuthenticated()).andReturn(true);
		expect(s.isAdmin()).andReturn(true);
		products.productsDAO.checkProductNotExists(toAdd.getProductCode());
		expectLastCall().andThrow(new ProductExistsException(coke()));

		testAdd();
	}

	@Test(expected = InvalidCategoryException.class)
	public void testAddProductInvalidCategory() throws Exception {
		expect(s.isAuthenticated()).andReturn(true);
		expect(s.isAdmin()).andReturn(true);
		products.productsDAO.checkProductNotExists(toAdd.getProductCode());
		int categoryId = toAdd.getCategory().getId();
		expect(products.categoryDAO.findCategory(categoryId)).andThrow(new InvalidCategoryException(categoryId));

		testAdd();
	}

	@Test
	public void testAddProduct() throws Exception {
		expect(s.isAuthenticated()).andReturn(true);
		expect(s.isAdmin()).andReturn(true);
		products.productsDAO.checkProductNotExists(toAdd.getProductCode());
		expect(products.categoryDAO.findCategory(toAdd.getCategory().getId())).andReturn(toAdd.getCategory());
		products.productsDAO.add(toAdd);

		testAdd();
	}

	private void testAdd() {
		mockManager.replay();

		try {
			products.addProduct(toAdd.getProductCode(), toAdd.getDescription(),
					toAdd.getCategory().getId(), toAdd.getInStock(), toAdd.getStockLowMark(),
					CurrencyUtils.toCents(toAdd.getCost()), CurrencyUtils.toPercent(toAdd.getMarkup()));
			mockManager.verify();
		} catch (FridgeException e) {
			mockManager.verify();
			throw e;
		}
	}

	@Test(expected = AuthenticationException.class)
	public void testUpdateProductInvalidHMac() throws Exception {
		expect(s.isAuthenticated()).andReturn(false);

		testUpdate();
	}

	@Test(expected = AccessDeniedException.class)
	public void testUpdateProductNotAdmin() throws Exception {
		expect(s.isAuthenticated()).andReturn(true);
		expect(s.isAdmin()).andReturn(false);
		expect(s.getUser()).andReturn(u);
		expect(u.getUsername()).andReturn(USERNAME);

		testUpdate();
	}

	@Test(expected = InvalidProductException.class)
	public void testUpdateProductNotFound() throws Exception {
		expect(s.isAuthenticated()).andReturn(true);
		expect(s.isAdmin()).andReturn(true);
		expect(products.productsDAO.findProduct(toUpdate.getProductCode())).andThrow(new InvalidProductException(toUpdate.getProductCode()));

		testUpdate();
	}

	@Test(expected = InvalidCategoryException.class)
	public void testUpdateProductInvalidCategory() throws Exception {
		expect(s.isAuthenticated()).andReturn(true);
		expect(s.isAdmin()).andReturn(true);
		expect(products.productsDAO.findProduct(toUpdate.getProductCode())).andReturn(cookie());
		expect(products.categoryDAO.findCategory(chocolateCategory().getId())).andThrow(new InvalidCategoryException(chocolateCategory().getId()));

		testUpdate();
	}

	@Test
	public void testUpdateProduct() throws Exception {
		expect(s.isAuthenticated()).andReturn(true);
		expect(s.isAdmin()).andReturn(true);
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
			products.updateProduct(toUpdate.getProductCode(), toUpdate.getDescription(),
					chocolateCategory().getId(), toUpdate.getStockLowMark(),
					toCents(toUpdate.getCost()), toPercent(toUpdate.getMarkup()), false);
			mockManager.verify();
		} catch (FridgeException e) {
			mockManager.verify();
			throw e;
		}
	}
}
