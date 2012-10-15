package memphis.fridge.server.services;

import java.math.BigDecimal;

import com.google.inject.Inject;
import memphis.fridge.dao.UserDAO;
import memphis.fridge.domain.User;
import memphis.fridge.exceptions.*;
import memphis.fridge.protocol.Messages;
import memphis.fridge.server.ioc.AuthModule;
import memphis.fridge.server.ioc.SessionState;
import memphis.fridge.test.*;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static memphis.fridge.utils.CurrencyUtils.toCents;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
    private final String fromUser = "FROMUSER";
    private final String toUser = "TOUSER";
    private final int amount = 0;
    private final String hmac = "HMAC";

    @Inject
    @InjectMocks
    Transfer transfer;

    @Inject
    @Mock
    UserDAO users;

    @Inject
    @Mock
    User user;

    @Inject
    @Mock
    SessionState s;

    @Before
    public void setUp() {
        mocks.reset();

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
        doThrow(new InvalidUserException(toUser))
                .when(users).checkValidUser(toUser);

        test(amount);
    }

    @Test(expected = InvalidAmountException.class)
    public void testTransferBadAmount() throws Exception {
        test(-1);
    }

    @Test
    public void testTransferZero() throws Exception {
        BigDecimal balance = new BigDecimal("10.00");

        when(users.retrieveUser(fromUser)).thenReturn(user);
        when(user.getBalance()).thenReturn(balance);

        Messages.TransactionResponse r = test(0);

        assertEquals(1000, r.getBalance());
        assertEquals(0, r.getCost());
    }

    @Test(expected = InsufficientFundsException.class)
    public void testTransferInsufficientFunds() throws Exception {
        BigDecimal amount = new BigDecimal("10.00");

        doThrow(new InsufficientFundsException(fromUser, BigDecimal.ZERO))
                .when(users).checkSufficientBalance(fromUser, amount);

        test(1000);
    }

    @Test
    public void testTransfer() throws Exception {
        BigDecimal amount = new BigDecimal("10.00");
        BigDecimal balance = new BigDecimal("5.00");

        when(users.retrieveUser(fromUser)).thenReturn(user);
        when(user.getBalance()).thenReturn(balance);

        Messages.TransactionResponse r = test(1000);

        verify(users).transferFunds(fromUser, toUser, amount);

        assertEquals(toCents(balance), r.getBalance());
        assertEquals(1000, r.getCost());
    }

    private Messages.TransactionResponse test(int amount) {
        Messages.TransactionResponse r = transfer.transfer(Messages.TransferRequest.newBuilder()
                .setFromUser(fromUser)
                .setToUser(toUser)
                .setAmount(amount).build());
        assertNotNull(r);
        return r;
    }
}
