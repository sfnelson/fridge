package memphis.fridge.dao;

import javax.inject.Inject;
import javax.inject.Provider;
import memphis.fridge.domain.Nonce;
import memphis.fridge.exceptions.FridgeException;
import memphis.fridge.ioc.GuiceJPATestRunner;
import memphis.fridge.ioc.GuiceTestRunner;
import memphis.fridge.ioc.TestModule;
import org.junit.Test;
import org.junit.runner.RunWith;

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
	Provider<NonceDAO> dao;

	@Test
	@GuiceJPATestRunner.Rollback
	public void testGenerateNonce() throws Exception {
		String cnonce = Nonce.createToken();
		int time = (int) (System.currentTimeMillis() / 1000);
		Nonce nonce = dao.get().generateNonce(cnonce, time);
	}

	@Test(expected = FridgeException.class)
	@GuiceJPATestRunner.Rollback
	public void testFailOnOldNonce() throws Exception {
		String cnonce = Nonce.createToken();
		int time = (int) (System.currentTimeMillis() / 1000) - Nonce.VALID_PERIOD * 2;
		Nonce nonce = dao.get().generateNonce(cnonce, time);
	}

	@Test(expected = FridgeException.class)
	@GuiceJPATestRunner.Rollback
	public void testFailOnReplayNonce() throws Exception {
		String cnonce = Nonce.createToken();
		int time = (int) (System.currentTimeMillis() / 1000);
		Nonce nonce1 = dao.get().generateNonce(cnonce, time);
		Nonce nonce2 = dao.get().generateNonce(cnonce, time);
	}
}
