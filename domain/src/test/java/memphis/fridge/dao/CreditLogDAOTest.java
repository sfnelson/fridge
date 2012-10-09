package memphis.fridge.dao;

import java.math.BigDecimal;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.validation.Validator;
import memphis.fridge.domain.User;
import memphis.fridge.ioc.GuiceJPATestRunner;
import memphis.fridge.ioc.GuiceTestRunner;
import memphis.fridge.ioc.TestModule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static memphis.fridge.utils.CurrencyUtils.fromCents;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 9/10/12
 */
@RunWith(GuiceJPATestRunner.class)
@GuiceTestRunner.GuiceModules({
		@GuiceTestRunner.GuiceModule(TestModule.class)
})
public class CreditLogDAOTest {
	@Inject
	Provider<CreditLogDAO> creditLog;

	@Inject
	Provider<UserDAO> users;

	@Inject
	Provider<ProductsDAO> products;

	@Inject
	Validator validator;

	@Test
	@GuiceJPATestRunner.Rollback
	public void testCreatePurchase() throws Exception {
		User user = null;
		BigDecimal amount = fromCents(100);
		creditLog.get().createPurchase(user, amount);
	}
}
