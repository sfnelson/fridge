package memphis.fridge.dao;

import com.google.inject.persist.jpa.JpaPersistModule;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import memphis.fridge.test.data.*;
import memphis.fridge.test.data.UsersTable;
import memphis.fridge.test.data.UsersTable.CreateThreeUsers;
import memphis.fridge.domain.User;
import memphis.fridge.exceptions.InsufficientFundsException;
import memphis.fridge.exceptions.InvalidUserException;
import memphis.fridge.test.GuiceJPATest;
import memphis.fridge.test.TestModule;
import memphis.fridge.test.persistence.WithTestData;
import org.junit.Test;
import org.mockito.Mock;

import static memphis.fridge.test.data.UsersTable.assertUsersEquals;
import static memphis.fridge.utils.CryptUtils.md5;
import static memphis.fridge.utils.CurrencyUtils.fromCents;
import static org.junit.Assert.assertEquals;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 29/09/12
 */
@TestModule(value = JpaPersistModule.class, args = "FridgeTestDB")
@WithTestData(FridgeVariables.class)
public class UserDAOTest extends GuiceJPATest {

	@Inject
	UserDAO users;

	@Inject
	EntityManager em;

	@Inject
	@Mock
	FridgeDAO fridge;

	@Test(expected = InvalidUserException.class)
	public void testCheckInvalidUser() throws Exception {
		users.checkValidUser(Graduate.NAME);
	}

	@Test
	@WithTestData(Graduate.class)
	public void testCheckValidUser() throws Exception {
		users.checkValidUser(Graduate.NAME);
	}

	@Test(expected = InvalidUserException.class)
	public void testRetrievingInvalidUser() throws Exception {
		users.retrieveUser(Graduate.NAME);
	}

	@Test
	@WithTestData(CreateThreeUsers.class)
	public void testRetrievingUsers() throws Exception {
		assertEquals(Graduate.create(), users.retrieveUser(Graduate.NAME));
		assertEquals(Undergrad.create(), users.retrieveUser(Undergrad.NAME));
		assertEquals(Admin.create(), users.retrieveUser(Admin.NAME));
	}

	@Test
	public void testCreateGraduateUser() throws Exception {
		User grad = Graduate.create();

		users.createGraduateUser(Graduate.NAME, md5(Graduate.PASS), Graduate.REAL, Graduate.EMAIL);

		User result = users.retrieveUser(grad.getUsername());

		assertEquals(grad, result);

		users.addFunds(grad, fromCents(500));

		assertUsersEquals(grad, result);
	}

	@Test
	@WithTestData(UsersTable.CreateThreeUsers.class)
	public void testCheckSufficientBalance() throws Exception {
		users.checkSufficientBalance(Graduate.NAME, fromCents(800));
	}

	@Test(expected = InsufficientFundsException.class)
	@WithTestData(UsersTable.CreateThreeUsers.class)
	public void testCheckInsufficientBalance() throws Exception {
		users.checkSufficientBalance(Graduate.NAME, fromCents(1200));
	}

	@Test(expected = InvalidUserException.class)
	public void testCreateDuplicateUser() throws Exception {
		users.createGraduateUser(Graduate.NAME, md5(Graduate.PASS), Graduate.REAL, Graduate.EMAIL);
		users.createGraduateUser(Graduate.NAME, md5(Undergrad.PASS), Undergrad.REAL, Undergrad.EMAIL);
	}

	@Test
	@WithTestData(UsersTable.CreateThreeUsers.class)
	public void testSigning() throws Exception {
		User grad = Graduate.create();
		String nonce = "5343a3ce73gi79bf437e";
		int timestamp = 1348922421;
		String signature = "9ea5dcc062e487a5b301527c45687b39";

		String result = users.createHMAC(grad.getUsername(), nonce, timestamp, grad.getUsername());

		assertEquals(signature, result);
	}

	@Test
	@WithTestData(UsersTable.CreateThreeUsers.class)
	public void testGetPassword() throws Exception {
		User grad = Graduate.create();

		String password = users.getPassword(grad.getUsername());

		assertEquals(grad.getPassword(), password);
	}

	@Test
	@WithTestData(UsersTable.CreateThreeUsers.class)
	public void testVerifying() throws Exception {
		User grad = Graduate.create();
		String nonce = "5343a3ce73gi79bf437e";
		int timestamp = 1348922421;
		String signature = "9ea5dcc062e487a5b301527c45687b39";

		assertEquals(signature, users.createHMAC(grad.getUsername(), nonce, timestamp, Graduate.NAME));
		users.validateHMAC(grad.getUsername(), signature, nonce, timestamp, Graduate.NAME);
	}
}
