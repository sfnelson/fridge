package memphis.fridge.dao;

import java.math.BigDecimal;

import com.google.inject.persist.jpa.JpaPersistModule;
import javax.inject.Inject;
import memphis.fridge.data.FridgeVariables;
import memphis.fridge.test.GuiceJPATest;
import memphis.fridge.test.TestModule;
import memphis.fridge.test.persistence.WithTestData;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static memphis.fridge.utils.CurrencyUtils.fromCents;
import static memphis.fridge.utils.CurrencyUtils.fromPercent;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 7/10/12
 */
@TestModule(value = JpaPersistModule.class, args = "FridgeTestDB")
@WithTestData(FridgeVariables.class)
public class FridgeDAOTest extends GuiceJPATest {

	@Inject
	FridgeDAO fridge;

	@Test
	public void testGetMinimumUserBalance() throws Exception {
		BigDecimal tax = fridge.getMinimumUserBalance();
		assertNotNull(tax);
		assertEquals(fromCents(-500), tax);
	}

	@Test
	public void testGetGraduateDiscount() throws Exception {
		BigDecimal tax = fridge.getGraduateDiscount();
		assertNotNull(tax);
		assertEquals(fromPercent(50), tax);
	}

	@Test
	public void testGetMinimumAdministratorBalance() throws Exception {
		BigDecimal tax = fridge.getMinimumAdministratorBalance();
		assertNotNull(tax);
		assertEquals(fromCents(-5000), tax);
	}
}
