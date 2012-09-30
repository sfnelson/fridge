package memphis.fridge.server.services;

import java.util.Date;

import memphis.fridge.dao.NonceDAO;
import memphis.fridge.dao.UserDAO;
import memphis.fridge.domain.Nonce;
import memphis.fridge.server.io.ResponseSerializer;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import static org.easymock.EasyMock.*;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 30/09/12
 */
public class GenerateNonceTest {

	GenerateNonce service;
	UserDAO users;
	NonceDAO nonces;

	@Before
	public void setUp() throws Exception {
		users = EasyMock.createMock(UserDAO.class);
		nonces = EasyMock.createMock(NonceDAO.class);

		service = new GenerateNonce();
		service.users = users;
		service.nonces = nonces;
	}

	@Test
	public void testGenerateNonce() throws Exception {
		String username = "foo";
		int timestamp = 12345;
		String snonce = "bazbarfoo";
		String cnonce = "foobarbaz";
		String hmac = "HMAC'ED";
		Nonce newNonce = new Nonce(snonce, cnonce, new Date());

		ResponseSerializer resp = EasyMock.createMock(ResponseSerializer.class);

		// setup
		reset(users, nonces, resp);
		users.validateHMAC(username, hmac, cnonce, timestamp, username);
		expect(nonces.generateNonce(cnonce, timestamp)).andReturn(newNonce);
		expect(users.createHMAC(username, snonce, cnonce)).andReturn(hmac);

		resp.visitString("snonce", snonce);
		resp.visitString("hmac", hmac);

		// test
		replay(users, nonces, resp);
		service.generateNonce(cnonce, timestamp, username, hmac).visitResponse(resp);

		// verify
		verify(users, nonces, resp);

	}
}
