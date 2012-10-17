package memphis.fridge.server.services;

import memphis.fridge.domain.User;
import memphis.fridge.exceptions.AccessDeniedException;
import memphis.fridge.exceptions.AuthenticationException;
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

import javax.inject.Inject;

import static memphis.fridge.test.data.Utils.*;
import static memphis.fridge.utils.CurrencyUtils.toCents;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

/**
 * @author stephen
 */
@RunWith(GuiceTestRunner.class)
@TestModule(AuthModule.class)
public class UsersTest {

    @Inject
    @ClassRule
    public static GuiceMockitoProvider mocks;

    @Inject
    @InjectMocks
    Users users;

    @Inject
    @Mock
    SessionState s;

    @Before
    public void setUp() throws Exception {
        mocks.reset();

        when(s.isAuthenticated()).thenReturn(true);
        when(s.getUser()).thenReturn(Graduate.create());
        when(users.users.retrieveUser(Graduate.NAME)).thenReturn(Graduate.create());
    }

    @Test(expected = AuthenticationException.class)
    public void testGetAccountDetailsNotAuthenticated() throws Exception {
        when(s.isAuthenticated()).thenReturn(false);

        users.getAccountDetails();
    }

    @Test
    public void testGetAccountDetails() throws Exception {
        Messages.AccountResponse resp = users.getAccountDetails();

        verify(Graduate.create(), resp);
    }

    @Test(expected = AccessDeniedException.class)
    public void testGetAccountDetailsFromOtherNotAdmin() throws Exception {
        users.getAccountDetails(Undergrad.NAME);
    }

    @Test(expected = InvalidUserException.class)
    public void testGetAccountDetailsAdminNoSuchAccount() throws Exception {
        when(s.getUser()).thenReturn(Admin.create());
        when(s.isAdmin()).thenReturn(true);
        when(users.users.retrieveUser("{FOOBAR}")).thenThrow(InvalidUserException.class);

        users.getAccountDetails("{FOOBAR}");
    }

    @Test
    public void testGetAccountDetailsAdmin() throws Exception {
        when(s.getUser()).thenReturn(Admin.create());
        when(s.isAdmin()).thenReturn(true);

        users.getAccountDetails(Graduate.NAME);
        Messages.AccountResponse resp = users.getAccountDetails(Graduate.NAME);

        verify(Graduate.create(), resp);
    }

    private void verify(User expected, Messages.AccountResponse resp) {
        assertEquals(expected.getUsername(), resp.getUsername());
        assertEquals(expected.getRealName(), resp.getFullName());
        assertEquals(expected.getEmail(), resp.getEmail());
        assertEquals(toCents(expected.getBalance()), resp.getBalance());
        assertEquals(expected.isEnabled(), resp.getIsEnabled());
        assertEquals(expected.isAdmin(), resp.getIsAdmin());
        assertEquals(expected.isGrad(), resp.getIsGrad());
    }
}
