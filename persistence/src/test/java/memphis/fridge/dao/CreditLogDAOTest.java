package memphis.fridge.dao;

import java.math.BigDecimal;

import com.google.inject.persist.jpa.JpaPersistModule;
import javax.inject.Inject;

import memphis.fridge.test.data.Graduate;
import memphis.fridge.domain.User;
import memphis.fridge.test.GuiceJPATest;
import memphis.fridge.test.TestModule;
import memphis.fridge.test.persistence.WithTestData;
import org.junit.Test;

import static memphis.fridge.utils.CurrencyUtils.fromCents;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 9/10/12
 */
@TestModule(value = JpaPersistModule.class, args = "FridgeTestDB")
public class CreditLogDAOTest extends GuiceJPATest {

	@Inject
	CreditLogDAO creditLog;

	@Inject
	UserDAO users;

	@Test
	@WithTestData(Graduate.class)
	public void testCreatePurchase() throws Exception {
		User user = users.retrieveUser(Graduate.NAME);
		BigDecimal amount = fromCents(100);
		creditLog.createPurchase(user, amount);
	}
}
