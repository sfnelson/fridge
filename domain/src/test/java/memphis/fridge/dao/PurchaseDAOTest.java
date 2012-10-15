package memphis.fridge.dao;

import java.math.BigDecimal;
import java.util.Set;

import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import memphis.fridge.data.Users;
import memphis.fridge.domain.Product;
import memphis.fridge.domain.Purchase;
import memphis.fridge.domain.User;
import memphis.fridge.ioc.TestModuleWithValidator;
import memphis.fridge.test.GuiceJPATest;
import memphis.fridge.test.TestModule;
import memphis.fridge.test.persistence.WithTestData;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 9/10/12
 */
@TestModule(value = TestModuleWithValidator.class, args = "FridgeTestDB")
public class PurchaseDAOTest extends GuiceJPATest {

	@Inject
	PurchaseDAO purchase;

	@Inject
	UserDAO users;

	@Inject
	ProductsDAO products;

	@Inject
	Validator validator;

	@Test
	public void testCreatePurchaseNoUser() throws Exception {
		User user = null;
		Product product = products.findProduct("CC");
		Purchase p = purchase.createPurchase(user, product, 1, BigDecimal.ONE, BigDecimal.ONE);

		Set<ConstraintViolation<Purchase>> violations = validator.validate(p);
		assertEquals(violations.toString(), 0, violations.size());
	}

	@Test
	@WithTestData(Users.Graduate.class)
	public void testCreatePurchase() throws Exception {
		User user = users.retrieveUser(Users.Graduate.NAME);
		Product product = products.findProduct("CC");
		Purchase p = purchase.createPurchase(user, product, 1, BigDecimal.ONE, BigDecimal.ONE);

		Set<ConstraintViolation<Purchase>> violations = validator.validate(p);
		assertEquals(violations.toString(), 0, violations.size());
	}
}
