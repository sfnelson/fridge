package memphis.fridge.dao;

import java.util.List;

import com.google.inject.persist.jpa.JpaPersistModule;
import javax.inject.Inject;
import javax.inject.Provider;
import memphis.fridge.data.Products;
import memphis.fridge.domain.Product;
import memphis.fridge.exceptions.InsufficientStockException;
import memphis.fridge.test.GuiceJPATest;
import memphis.fridge.test.TestModule;
import memphis.fridge.test.persistence.WithTestData;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 7/10/12
 */
@TestModule(value = JpaPersistModule.class, args = "FridgeTestDB")
@WithTestData(Products.class)
public class ProductsDAOTest extends GuiceJPATest {

	@Inject
	Provider<ProductsDAO> products;

	@Test
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

	@Test
	public void testFindProductNotPresent() throws Exception {
		Product nan = products.get().findProduct("NaN");
		assertNull(nan);
	}

	@Test
	public void testConsumeProduct() throws Exception {
		Product coke = products.get().findProduct("CC");
		int stock = coke.getInStock();
		products.get().consumeProduct(coke, 20);
		assertEquals(stock - 20, products.get().findProduct("CC").getInStock());
	}

	@Test(expected = InsufficientStockException.class)
	public void testConsumeProductTooLittleStock() throws Exception {
		Product coke = products.get().findProduct("CC");
		int stock = coke.getInStock();
		products.get().consumeProduct(coke, 24);
	}

	@Test(expected = InsufficientStockException.class)
	public void testConsumeProductTooLittleStock2() throws Exception {
		Product coke = products.get().findProduct("CC");
		products.get().consumeProduct(coke, 20);
		products.get().consumeProduct(coke, 4);
	}

	@Test
	public void testGetEnabledProducts() throws Exception {
		List<Product> enabled = products.get().getEnabledProducts();
		assertNotNull(enabled);
		assertEquals(2, enabled.size());
	}
}
