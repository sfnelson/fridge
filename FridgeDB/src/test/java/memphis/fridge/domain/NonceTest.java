package memphis.fridge.domain;

import org.junit.Test;

import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 29/09/12
 */
public class NonceTest {
	@Test
	public void testCreateToken() throws Exception {
		String token = Nonce.createToken();
		assertTrue(token.matches("[" + Nonce.NONCE_CHARS + "]{20}"));
		assertThat(Nonce.createToken(), not(Nonce.createToken()));
	}
}
