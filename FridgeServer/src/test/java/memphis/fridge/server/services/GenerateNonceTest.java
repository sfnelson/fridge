package memphis.fridge.server.services;

import java.util.Date;

import javax.inject.Inject;
import memphis.fridge.dao.NonceDAO;
import memphis.fridge.dao.UserDAO;
import memphis.fridge.domain.Nonce;
import memphis.fridge.server.io.ResponseSerializer;
import memphis.fridge.server.ioc.MockInjectingRunner;
import memphis.fridge.server.ioc.MockInjectingRunner.Mock;
import memphis.fridge.server.ioc.MockInjectingRunner.MockManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.reset;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 30/09/12
 */
@RunWith(MockInjectingRunner.class)
@MockInjectingRunner.ToInject({GenerateNonce.class})
public class GenerateNonceTest {

	@Inject
	GenerateNonce service;

	@Inject
	MockManager mocks;

	@Inject
	@Mock
	UserDAO users;

	@Inject
	@Mock
	NonceDAO nonces;

	@Inject
	@Mock
	ResponseSerializer.ObjectSerializer resp;

	@Before
	public void setUp() throws Exception {
		mocks.reset();
	}

	@Test
	public void testGenerateNonce() throws Exception {
		String username = "foo";
		int timestamp = 12345;
		String snonce = "bazbarfoo";
		String cnonce = "foobarbaz";
		String hmac = "HMAC'ED";
		Nonce newNonce = new Nonce(snonce, cnonce, new Date());

		// setup
		reset(users, nonces, resp);
		users.validateHMAC(username, hmac, cnonce, timestamp, username);
		expect(nonces.generateNonce(cnonce, timestamp)).andReturn(newNonce);
		expect(users.createHMAC(username, snonce, cnonce)).andReturn(hmac);

		resp.visitString("snonce", snonce);
		resp.visitString("hmac", hmac);

		// test
		mocks.replay();
		service.generateNonce(cnonce, timestamp, username, hmac).visit(resp);

		// verify
		mocks.verify();

	}
}
