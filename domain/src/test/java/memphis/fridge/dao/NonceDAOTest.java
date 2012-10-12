package memphis.fridge.dao;

import java.util.Date;

import javax.inject.Inject;
import javax.inject.Provider;
import memphis.fridge.domain.Nonce;
import memphis.fridge.domain.User;
import memphis.fridge.exceptions.AuthenticationException;
import memphis.fridge.exceptions.FridgeException;
import memphis.fridge.ioc.GuiceJPATestRunner;
import memphis.fridge.ioc.GuiceTestRunner;
import memphis.fridge.ioc.TestModule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static memphis.fridge.utils.CryptUtils.generateNonceToken;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 29/09/12
 */
@RunWith(GuiceJPATestRunner.class)
@GuiceTestRunner.GuiceModules({
		@GuiceTestRunner.GuiceModule(TestModule.class)
})
public class NonceDAOTest {

	@Inject
	Provider<UserDAO> users;

	@Inject
	Provider<NonceDAO> dao;

	@Test
	@GuiceJPATestRunner.Rollback
	public void testConsumeNonce() throws Exception {
		User user = users.get().retrieveUser("stephen");
		String nonce = generateNonceToken();
		Date timestamp = new Date();
		dao.get().consumeNonce(user, nonce, timestamp);
	}

	@Test(expected = AuthenticationException.class)
	@GuiceJPATestRunner.Rollback
	public void testConsumeNonceFailOnReplay() throws Exception {
		User user = users.get().retrieveUser("stephen");
		String nonce = generateNonceToken();
		Date timestamp = new Date();
		dao.get().consumeNonce(user, nonce, timestamp);
		dao.get().consumeNonce(user, nonce, timestamp); // should throw
	}

	@Test
	@GuiceJPATestRunner.Rollback
	public void testConsumeNonceVariations() throws Exception {
		User user1 = users.get().retrieveUser("stephen");
		User user2 = users.get().retrieveUser("chris");
		String nonce1 = generateNonceToken();
		String nonce2 = generateNonceToken();
		Date timestamp1 = new Date();
		Date timestamp2 = new Date(timestamp1.getTime() + 1000);
		dao.get().consumeNonce(user1, nonce1, timestamp1);
		dao.get().consumeNonce(user2, nonce1, timestamp1);
		dao.get().consumeNonce(user1, nonce2, timestamp1);
		dao.get().consumeNonce(user2, nonce2, timestamp1);
		dao.get().consumeNonce(user1, nonce1, timestamp2);
		dao.get().consumeNonce(user2, nonce1, timestamp2);
		dao.get().consumeNonce(user1, nonce2, timestamp2);
		dao.get().consumeNonce(user2, nonce2, timestamp2);
	}

	@Test(expected = FridgeException.class)
	@GuiceJPATestRunner.Rollback
	public void testFailOnOldNonce() throws Exception {
		User user = users.get().retrieveUser("stephen");
		String nonce = generateNonceToken();
		Date timestamp = new Date(System.currentTimeMillis() - Nonce.VALID_PERIOD * 2000);
		dao.get().consumeNonce(user, nonce, timestamp);
	}
}
