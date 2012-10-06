package memphis.fridge.server.services;

import java.math.BigDecimal;

import com.google.inject.Inject;
import memphis.fridge.dao.UserDAO;
import memphis.fridge.domain.User;
import memphis.fridge.exceptions.FridgeException;
import memphis.fridge.exceptions.InsufficientFundsException;
import memphis.fridge.exceptions.InvalidAmountException;
import memphis.fridge.exceptions.InvalidUserException;
import memphis.fridge.server.io.Response;
import memphis.fridge.server.io.ResponseSerializer;
import memphis.fridge.server.ioc.MockInjectingRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertNotNull;
import static memphis.fridge.utils.CurrencyUtils.toCents;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 30/09/12
 */
@RunWith(MockInjectingRunner.class)
@MockInjectingRunner.ToInject({Transfer.class})
public class TransferTest {

	private final String snonce = "SNONCE";
	private final String fromUser = "FROMUSER";
	private final String toUser = "TOUSER";
	private final int amount = 0;
	private final String hmac = "HMAC";

	@Inject
	Transfer transfer;

	@Inject
	@MockInjectingRunner.Mock
	UserDAO users;

	@Inject
	@MockInjectingRunner.Mock
	User user;

	@Inject
	@MockInjectingRunner.Mock
	ResponseSerializer resp;

	@Inject
	MockInjectingRunner.MockManager mocks;

	@Before
	public void setUp() {
		mocks.reset();
	}

	@Test(expected = FridgeException.class)
	public void testTransferBadHMAC() throws Exception {
		users.validateHMAC(fromUser, hmac, snonce, fromUser, toUser, amount);
		expectLastCall().andThrow(new FridgeException(1, "invalid hmac"));

		test(amount);
	}

	@Test(expected = InvalidUserException.class)
	public void testTransferBadToUser() throws Exception {
		users.validateHMAC(fromUser, hmac, snonce, fromUser, toUser, amount);
		users.checkValidUser(toUser);
		expectLastCall().andThrow(new InvalidUserException(toUser));

		test(amount);
	}

	@Test(expected = InvalidAmountException.class)
	public void testTransferBadAmount() throws Exception {
		users.validateHMAC(fromUser, hmac, snonce, fromUser, toUser, -1);
		users.checkValidUser(toUser);

		test(-1);
	}

	@Test
	public void testTransferZero() throws Exception {
		BigDecimal balance = new BigDecimal("10.00");

		users.validateHMAC(fromUser, hmac, snonce, fromUser, toUser, 0);
		users.checkValidUser(toUser);
		expect(users.retrieveUser(fromUser)).andReturn(user);
		expect(user.getBalance()).andReturn(balance);
		expect(users.createHMAC(fromUser, snonce, 1000)).andReturn(hmac);

		resp.visitInteger("balance", 1000);
		resp.visitString("hmac", hmac);

		test(0);
	}

	@Test(expected = InsufficientFundsException.class)
	public void testTransferInsufficientFunds() throws Exception {
		BigDecimal amount = new BigDecimal("10.00");

		users.validateHMAC(fromUser, hmac, snonce, fromUser, toUser, 1000);
		users.checkValidUser(toUser);
		users.checkSufficientBalance(fromUser, amount);
		expectLastCall().andThrow(new InsufficientFundsException(fromUser, BigDecimal.ZERO));

		test(1000);
	}

	@Test
	public void testTransfer() throws Exception {
		BigDecimal amount = new BigDecimal("10.00");
		BigDecimal balance = new BigDecimal("5.00");

		users.validateHMAC(fromUser, hmac, snonce, fromUser, toUser, 1000);
		users.checkValidUser(toUser);
		users.checkSufficientBalance(fromUser, amount);
		users.transferFunds(fromUser, toUser, amount);
		expect(users.retrieveUser(fromUser)).andReturn(user);
		expect(user.getBalance()).andReturn(balance);
		expect(users.createHMAC(fromUser, snonce, 500)).andReturn(hmac);

		resp.visitInteger("balance", toCents(balance));
		resp.visitString("hmac", hmac);

		test(1000);
	}

	private void test(int amount) {
		mocks.replay();
		try {
			Response response = transfer.transfer(snonce, fromUser, toUser, amount, hmac);
			assertNotNull(response);
			response.visitResponse(resp);
		} catch (FridgeException ex) {
			mocks.verify();
			throw ex;
		}
		mocks.verify();
	}
}
