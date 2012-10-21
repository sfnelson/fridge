package memphis.fridge.server.services;

import java.math.BigDecimal;

import com.google.inject.Inject;
import memphis.fridge.dao.UserDAO;
import memphis.fridge.domain.User;
import memphis.fridge.exceptions.AuthenticationException;
import memphis.fridge.exceptions.InsufficientFundsException;
import memphis.fridge.exceptions.InvalidAmountException;
import memphis.fridge.exceptions.InvalidUserException;
import memphis.fridge.protocol.Messages;
import memphis.fridge.server.ioc.AuthModule;
import memphis.fridge.server.ioc.SessionState;
import memphis.fridge.test.*;
import memphis.fridge.test.data.Admin;
import memphis.fridge.test.data.Graduate;
import memphis.fridge.test.data.Undergrad;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static memphis.fridge.utils.CurrencyUtils.toCents;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 30/09/12
 */
@RunWith(GuiceTestRunner.class)
@TestModule(AuthModule.class)
public class TransferTest {

	@ClassRule
	@Inject
	public static GuiceMockitoProvider mocks;

	private final String snonce = "SNONCE";
	private final int amount = 0;
	private final String hmac = "HMAC";

	@Inject
	@InjectMocks
	Users transfer;

	@Inject
	@Mock
	UserDAO users;

	@Inject
	@Mock
	SessionState s;

	User fromUser;
	User toUser;
	User adminUser;

	@Before
	public void setUp() {
		mocks.reset();

		fromUser = Graduate.create();
		toUser = Undergrad.create();
		adminUser = Admin.create();

		when(s.isAuthenticated()).thenReturn(true);
	}

	@Test(expected = AuthenticationException.class)
	public void testNotAuthenticated() throws Exception {
		when(s.isAuthenticated()).thenReturn(false);
		test(amount);
	}

	@Test(expected = InvalidUserException.class)
	public void testTransferBadToUser() throws Exception {
		when(s.isAuthenticated()).thenReturn(true);
		doThrow(new InvalidUserException(toUser.getUsername()))
				.when(users).checkValidUser(toUser.getUsername());

		test(amount);
	}

	@Test(expected = InvalidAmountException.class)
	public void testTransferBadAmount() throws Exception {
		when(s.getUser()).thenReturn(fromUser);

		test(-1);
	}

	@Test
	public void testTransferZero() throws Exception {
		when(s.getUser()).thenReturn(fromUser);
		when(users.retrieveUser(fromUser.getUsername())).thenReturn(fromUser);

		Messages.TransactionResponse r = test(0);

		assertEquals(500, r.getBalance());
		assertEquals(0, r.getCost());
	}

	@Test(expected = InsufficientFundsException.class)
	public void testTransferInsufficientFunds() throws Exception {
		BigDecimal amount = new BigDecimal("10.00");

		when(s.getUser()).thenReturn(fromUser);
		doThrow(new InsufficientFundsException(fromUser.getUsername(), BigDecimal.ZERO))
				.when(users).checkSufficientBalance(fromUser.getUsername(), amount);

		test(1000);
	}

	@Test
	public void testTransfer() throws Exception {
		BigDecimal amount = new BigDecimal("10.00");
		BigDecimal balance = new BigDecimal("5.00");

		when(s.getUser()).thenReturn(fromUser);
		when(users.retrieveUser(fromUser.getUsername())).thenReturn(fromUser);
		when(users.retrieveUser(toUser.getUsername())).thenReturn(toUser);

		Messages.TransactionResponse r = test(1000);

		verify(users).transferFunds(fromUser, toUser, amount);

		assertEquals(toCents(balance), r.getBalance());
		assertEquals(1000, r.getCost());
	}

	private Messages.TransactionResponse test(int amount) {
		Messages.TransactionResponse r = transfer.transfer(Messages.TransferRequest.newBuilder()
				.setFromUser(fromUser.getUsername())
				.setToUser(toUser.getUsername())
				.setAmount(amount).build());
		assertNotNull(r);
		return r;
	}
}
