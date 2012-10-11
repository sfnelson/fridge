package memphis.fridge.server.services;

import java.math.BigDecimal;

import com.google.inject.Inject;
import memphis.fridge.dao.UserDAO;
import memphis.fridge.domain.User;
import memphis.fridge.exceptions.*;
import memphis.fridge.protocol.Messages;
import memphis.fridge.server.ioc.AuthModule;
import memphis.fridge.server.ioc.MockInjectingRunner;
import memphis.fridge.server.ioc.SessionState;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;
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
@MockInjectingRunner.WithModules({AuthModule.class})
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
	MockInjectingRunner.MockManager mocks;

	@Inject
	@MockInjectingRunner.Mock
	SessionState s;

	@Before
	public void setUp() {
		mocks.reset();
	}

	@Test(expected = AuthenticationException.class)
	public void testNotAuthenticated() throws Exception {
		expect(s.isAuthenticated()).andReturn(false);
		test(amount);
	}

	@Test(expected = InvalidUserException.class)
	public void testTransferBadToUser() throws Exception {
		expect(s.isAuthenticated()).andReturn(true);
		users.checkValidUser(toUser);
		expectLastCall().andThrow(new InvalidUserException(toUser));

		test(amount);
	}

	@Test(expected = InvalidAmountException.class)
	public void testTransferBadAmount() throws Exception {
		expect(s.isAuthenticated()).andReturn(true);
		users.checkValidUser(toUser);

		test(-1);
	}

	@Test
	public void testTransferZero() throws Exception {
		BigDecimal balance = new BigDecimal("10.00");

		expect(s.isAuthenticated()).andReturn(true);
		users.checkValidUser(toUser);
		expect(users.retrieveUser(fromUser)).andReturn(user);
		expect(user.getBalance()).andReturn(balance);

		Messages.TransactionResponse r = test(0);
		assertEquals(1000, r.getBalance());
		assertEquals(0, r.getCost());
	}

	@Test(expected = InsufficientFundsException.class)
	public void testTransferInsufficientFunds() throws Exception {
		BigDecimal amount = new BigDecimal("10.00");

		expect(s.isAuthenticated()).andReturn(true);
		users.checkValidUser(toUser);
		users.checkSufficientBalance(fromUser, amount);
		expectLastCall().andThrow(new InsufficientFundsException(fromUser, BigDecimal.ZERO));

		test(1000);
	}

	@Test
	public void testTransfer() throws Exception {
		BigDecimal amount = new BigDecimal("10.00");
		BigDecimal balance = new BigDecimal("5.00");

		expect(s.isAuthenticated()).andReturn(true);
		users.checkValidUser(toUser);
		users.checkSufficientBalance(fromUser, amount);
		users.transferFunds(fromUser, toUser, amount);
		expect(users.retrieveUser(fromUser)).andReturn(user);
		expect(user.getBalance()).andReturn(balance);

		Messages.TransactionResponse r = test(1000);
		assertEquals(toCents(balance), r.getBalance());
		assertEquals(1000, r.getCost());
	}

	private Messages.TransactionResponse test(int amount) {
		Messages.TransactionResponse r;
		mocks.replay();
		try {
			r = transfer.transfer(Messages.TransferRequest.newBuilder()
					.setFromUser(fromUser)
					.setToUser(toUser)
					.setAmount(amount).build());
			assertNotNull(r);
		} catch (FridgeException ex) {
			mocks.verify();
			throw ex;
		}
		mocks.verify();
		return r;
	}
}
