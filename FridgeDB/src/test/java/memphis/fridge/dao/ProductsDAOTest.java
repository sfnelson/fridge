package memphis.fridge.dao;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;
import memphis.fridge.domain.Product;
import memphis.fridge.exceptions.InsufficientStockException;
import memphis.fridge.ioc.GuiceJPATestRunner;
import memphis.fridge.ioc.GuiceTestRunner;
import memphis.fridge.ioc.TestModule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.*;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 7/10/12
 */
@RunWith(GuiceJPATestRunner.class)
@GuiceTestRunner.GuiceModules({
		@GuiceTestRunner.GuiceModule(TestModule.class)
})
public class ProductsDAOTest {

	@Inject
	Provider<ProductsDAO> products;

	@Test
	@GuiceJPATestRunner.Rollback
	public void testFindProduct() throws Exception {
		Product coke = products.get().findProduct("CC");
		assertNotNull(coke);
		assertEquals("CC", coke.getProductCode());
		assertEquals("Coke", coke.getDescription());
		assertNotNull(coke.getCost());
		assertNotNull(coke.getMarkup());
		assertEquals("Drinks", coke.getCategory().getTitle());
		assertEquals(1, coke.getCategory().getDisplaySequence());
	}

	@GuiceJPATestRunner.Rollback
	public void testFindProductNotPresent() throws Exception {
		Product nan = products.get().findProduct("NaN");
		assertNull(nan);
	}

	@Test
	@GuiceJPATestRunner.Rollback
	public void testConsumeProduct() throws Exception {
		Product coke = products.get().findProduct("CC");
		int stock = coke.getInStock();
		products.get().consumeProduct(coke, 2);
		assertEquals(stock - 2, products.get().findProduct("CC").getInStock());
	}

	@Test(expected = InsufficientStockException.class)
	@GuiceJPATestRunner.Rollback
	public void testConsumeProductTooLittleStock() throws Exception {
		Product coke = products.get().findProduct("CC");
		int stock = coke.getInStock();
		products.get().consumeProduct(coke, 24);
	}

	@Test(expected = InsufficientStockException.class)
	@GuiceJPATestRunner.Rollback
	public void testConsumeProductTooLittleStock2() throws Exception {
		Product coke = products.get().findProduct("CC");
		products.get().consumeProduct(coke, 20);
		products.get().consumeProduct(coke, 4);
	}

	@Test
	@GuiceJPATestRunner.Rollback
	public void testGetEnabledProducts() throws Exception {
		List<Product> enabled = products.get().getEnabledProducts();
		assertNotNull(enabled);
		assertEquals(4, enabled.size());
	}
}
