package memphis.fridge.server.services;

import java.math.BigDecimal;

import memphis.fridge.dao.UserDAO;
import memphis.fridge.domain.User;
import memphis.fridge.exceptions.FridgeException;
import memphis.fridge.exceptions.InsufficientFundsException;
import memphis.fridge.exceptions.InvalidAmountException;
import memphis.fridge.exceptions.InvalidUserException;
import memphis.fridge.server.io.ResponseSerializer;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.easymock.EasyMock.*;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 30/09/12
 */
public class TransferTest {

	String snonce;
	String fromUser;
	String toUser;
	int amount;
	String hmac;

	Transfer transfer;

	UserDAO users;

	@Before
	public void setUp() {
		snonce = "SNONCE";
		fromUser = "FROMUSER";
		toUser = "TOUSER";
		amount = 0;
		hmac = "HMAC";

		transfer = new Transfer();
		users = EasyMock.createMock(UserDAO.class);

		transfer.users = users;

		reset(users);
	}

	@After
	public void tearDown() {
		verify(users);
	}

	@Test(expected = FridgeException.class)
	public void testTransferBadHMAC() throws Exception {
		users.validateHMAC(fromUser, hmac, snonce, fromUser, toUser, amount);
		expectLastCall().andThrow(new FridgeException(1, "invalid hmac"));

		replay(users);

		transfer.transfer(snonce, fromUser, toUser, amount, hmac);
	}

	@Test(expected = InvalidUserException.class)
	public void testTransferBadToUser() throws Exception {
		users.validateHMAC(fromUser, hmac, snonce, fromUser, toUser, amount);
		users.checkValidUser(toUser);
		expectLastCall().andThrow(new InvalidUserException(toUser));

		replay(users);

		transfer.transfer(snonce, fromUser, toUser, amount, hmac);
	}

	@Test(expected = InvalidAmountException.class)
	public void testTransferBadAmount() throws Exception {
		users.validateHMAC(fromUser, hmac, snonce, fromUser, toUser, -1);
		users.checkValidUser(toUser);

		replay(users);

		transfer.transfer(snonce, fromUser, toUser, -1, hmac);
	}

	@Test
	public void testTransferZero() throws Exception {
		User user = createMock(User.class);
		ResponseSerializer resp = createMock(ResponseSerializer.class);
		BigDecimal balance = new BigDecimal("10.00");

		users.validateHMAC(fromUser, hmac, snonce, fromUser, toUser, 0);
		users.checkValidUser(toUser);
		expect(users.retrieveUser(fromUser)).andReturn(user);
		expect(user.getBalance()).andReturn(balance);
		expect(users.createHMAC(fromUser, snonce, 1000)).andReturn(hmac);

		resp.visitInteger("balance", 1000);
		resp.visitString("hmac", hmac);

		replay(users, user, resp);

		transfer.transfer(snonce, fromUser, toUser, 0, hmac).visitResponse(resp);

		verify(user, resp);
	}

	@Test(expected = InsufficientFundsException.class)
	public void testTransferInsufficientFunds() throws Exception {
		BigDecimal amount = new BigDecimal("10.00");

		users.validateHMAC(fromUser, hmac, snonce, fromUser, toUser, 1000);
		users.checkValidUser(toUser);
		users.checkSufficientBalance(fromUser, amount);
		expectLastCall().andThrow(new InsufficientFundsException(fromUser, BigDecimal.ZERO));

		replay(users);

		transfer.transfer(snonce, fromUser, toUser, 1000, hmac);
	}

	@Test
	public void testTransfer() throws Exception {
		User user = createMock(User.class);
		ResponseSerializer resp = createMock(ResponseSerializer.class);
		BigDecimal amount = new BigDecimal("10.00");
		BigDecimal balance = new BigDecimal("5.00");

		users.validateHMAC(fromUser, hmac, snonce, fromUser, toUser, 1000);
		users.checkValidUser(toUser);
		users.checkSufficientBalance(fromUser, amount);
		users.transferFunds(fromUser, toUser, amount);
		expect(users.retrieveUser(fromUser)).andReturn(user);
		expect(user.getBalance()).andReturn(balance);
		expect(users.createHMAC(fromUser, snonce, 500)).andReturn(hmac);

		resp.visitInteger("balance", 500);
		resp.visitString("hmac", hmac);

		replay(users, user, resp);

		transfer.transfer(snonce, fromUser, toUser, 1000, hmac).visitResponse(resp);

		verify(user, resp);
	}
}
