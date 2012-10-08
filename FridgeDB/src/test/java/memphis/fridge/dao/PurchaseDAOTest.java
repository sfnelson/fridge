package memphis.fridge.dao;

import java.math.BigDecimal;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import memphis.fridge.domain.Product;
import memphis.fridge.domain.Purchase;
import memphis.fridge.domain.User;
import memphis.fridge.ioc.GuiceJPATestRunner;
import memphis.fridge.ioc.GuiceTestRunner;
import memphis.fridge.ioc.TestModule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 9/10/12
 */
@RunWith(GuiceJPATestRunner.class)
@GuiceTestRunner.GuiceModules({
		@GuiceTestRunner.GuiceModule(TestModule.class)
})
public class PurchaseDAOTest {

	@Inject
	Provider<PurchaseDAO> purchase;

	@Inject
	Provider<UserDAO> users;

	@Inject
	Provider<ProductsDAO> products;

	@Inject
	Validator validator;

	@Test
	@GuiceJPATestRunner.Rollback
	public void testCreatePurchaseNoUser() throws Exception {
		User user = null;
		Product product = products.get().findProduct("CC");
		Purchase p = purchase.get().createPurchase(user, product, 1, BigDecimal.ONE, BigDecimal.ONE);

		Set<ConstraintViolation<Purchase>> violations = validator.validate(p);
		assertEquals(violations.toString(), 0, violations.size());
	}

	@Test
	@GuiceJPATestRunner.Rollback
	public void testCreatePurchase() throws Exception {
		User user = users.get().retrieveUser("stephen");
		Product product = products.get().findProduct("CC");
		Purchase p = purchase.get().createPurchase(user, product, 1, BigDecimal.ONE, BigDecimal.ONE);

		Set<ConstraintViolation<Purchase>> violations = validator.validate(p);
		assertEquals(violations.toString(), 0, violations.size());
	}
}
