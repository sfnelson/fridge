package memphis.fridge.utils;

import java.util.List;

import com.google.common.collect.Lists;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.*;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 30/09/12
 */
public class CryptUtilsTest {
	@Test
	public void testGenerateNonceToken() throws Exception {
		String token = CryptUtils.generateNonceToken();
		assertTrue(token.matches("[" + CryptUtils.NONCE_CHARS + "]{20}"));
		assertThat(CryptUtils.generateNonceToken(), not(CryptUtils.generateNonceToken()));
	}

	@Test
	public void testSign() throws Exception {
		String cnonce = "5343a3ce73gi79bf437e";
		int timestamp = 1348922421;
		String username = "foo";
		String hmac = "6272b3ac2866224a3058d051d56e130a";
		String password = CryptUtils.md5("password");

		assertEquals(hmac, CryptUtils.sign(password, cnonce, timestamp, username));
	}

	@Test
	public void testSignPairs() throws Exception {
		String nonce = "rEQ7rxpXTrm4X2LDFv9f";
		String user = "stephen";
		List<Pair<String, Integer>> products = Lists.newArrayList(
				new Pair<String, Integer>("CC", 1));
		String hmac = "647dc073344749cb1e16990aa8f32916";

		String password = "29745cbf5649cfadee1e97d784ce7c33";

		assertEquals(hmac, CryptUtils.sign(password, nonce, user, products));
	}
}
