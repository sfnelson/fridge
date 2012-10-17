package memphis.fridge.dao;

import java.util.Date;

import com.google.inject.persist.jpa.JpaPersistModule;
import javax.inject.Inject;

import memphis.fridge.test.data.UsersTable;
import memphis.fridge.test.data.Graduate;
import memphis.fridge.test.data.Undergrad;
import memphis.fridge.domain.Nonce;
import memphis.fridge.domain.User;
import memphis.fridge.exceptions.AuthenticationException;
import memphis.fridge.test.GuiceJPATest;
import memphis.fridge.test.TestModule;
import memphis.fridge.test.persistence.WithTestData;
import org.junit.Test;

import static memphis.fridge.utils.CryptUtils.generateNonceToken;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 29/09/12
 */
@TestModule(value = JpaPersistModule.class, args = "FridgeTestDB")
public class NonceDAOTest extends GuiceJPATest {

	@Inject
	UserDAO users;

	@Inject
	NonceDAO dao;

	@Test
	@WithTestData(Graduate.class)
	public void testConsumeNonce() throws Exception {
		User user = users.retrieveUser(Graduate.NAME);
		String nonce = generateNonceToken();
		Date timestamp = new Date();
		dao.consumeNonce(user, nonce, timestamp);
	}

	@Test(expected = AuthenticationException.class)
	@WithTestData(Graduate.class)
	public void testConsumeNonceFailOnReplay() throws Exception {
		User user = users.retrieveUser(Graduate.NAME);
		String nonce = generateNonceToken();
		Date timestamp = new Date();
		dao.consumeNonce(user, nonce, timestamp);
		dao.consumeNonce(user, nonce, timestamp); // should throw
	}

	@Test
	@WithTestData(UsersTable.CreateThreeUsers.class)
	public void testConsumeNonceVariations() throws Exception {
		User user1 = users.retrieveUser(Graduate.NAME);
		User user2 = users.retrieveUser(Undergrad.NAME);
		String nonce1 = generateNonceToken();
		String nonce2 = generateNonceToken();
		Date timestamp1 = new Date();
		Date timestamp2 = new Date(timestamp1.getTime() + 1000);
		dao.consumeNonce(user1, nonce1, timestamp1);
		dao.consumeNonce(user2, nonce1, timestamp1);
		dao.consumeNonce(user1, nonce2, timestamp1);
		dao.consumeNonce(user2, nonce2, timestamp1);
		dao.consumeNonce(user1, nonce1, timestamp2);
		dao.consumeNonce(user2, nonce1, timestamp2);
		dao.consumeNonce(user1, nonce2, timestamp2);
		dao.consumeNonce(user2, nonce2, timestamp2);
	}

	@Test(expected = AuthenticationException.class)
	@WithTestData(Graduate.class)
	public void testFailOnOldNonce() throws Exception {
		User user = users.retrieveUser(Graduate.NAME);
		String nonce = generateNonceToken();
		Date timestamp = new Date(System.currentTimeMillis() - Nonce.VALID_PERIOD * 2000);
		dao.consumeNonce(user, nonce, timestamp);
	}
}
