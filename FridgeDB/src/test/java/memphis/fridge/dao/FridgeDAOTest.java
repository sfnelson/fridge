package memphis.fridge.dao;

import java.math.BigDecimal;

import javax.inject.Inject;
import javax.inject.Provider;
import memphis.fridge.ioc.GuiceJPATestRunner;
import memphis.fridge.ioc.GuiceTestRunner;
import memphis.fridge.ioc.TestModule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static memphis.fridge.utils.CurrencyUtils.fromPercent;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 7/10/12
 */
@RunWith(GuiceJPATestRunner.class)
@GuiceTestRunner.GuiceModules({
		@GuiceTestRunner.GuiceModule(TestModule.class)
})
public class FridgeDAOTest {

	@Inject
	Provider<FridgeDAO> fridge;

	@Test
	@GuiceJPATestRunner.Rollback
	public void testGetGraduateDiscount() throws Exception {
		BigDecimal tax = fridge.get().getGraduateDiscount();
		assertNotNull(tax);
		assertEquals(fromPercent(10), tax);
	}
}
