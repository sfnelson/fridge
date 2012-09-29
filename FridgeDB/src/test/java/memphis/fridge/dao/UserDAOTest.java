package memphis.fridge.dao;

import java.security.MessageDigest;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.xml.bind.DatatypeConverter;
import memphis.fridge.domain.User;
import memphis.fridge.ioc.GuiceJPATestRunner;
import memphis.fridge.ioc.GuiceTestRunner;
import memphis.fridge.ioc.TestModule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 29/09/12
 */
@RunWith(GuiceJPATestRunner.class)
@GuiceTestRunner.GuiceModules({
		@GuiceTestRunner.GuiceModule(TestModule.class)
})
public class UserDAOTest {

	@Inject
	Provider<UserDAO> users;

	@Test
	@GuiceJPATestRunner.Rollback
	public void testCreateGraduateUser() throws Exception {
		User e = createFooUser();
		e.setGrad(true);
		e.setSponsor(null);

		createGraduateUser(e);
		User a = users.get().retrieveUser(e.getUsername());

		assertUsersEquals(e, a);
	}

	@Test
	@GuiceJPATestRunner.Rollback
	public void testSigning() throws Exception {
		String cnonce = "5343a3ce73gi79bf437e";
		int timestamp = 1348922421;
		String username = "foo";
		String hmac = "6272b3ac2866224a3058d051d56e130a";

		User e = createFooUser();
		createGraduateUser(e);

		String result = users.get().createHMAC(e.getUsername(), cnonce, timestamp, username);

		assertEquals(hmac, result);
	}

	@Test
	@GuiceJPATestRunner.Rollback
	public void testGetPassword() throws Exception {
		User e = createFooUser();
		createGraduateUser(e);

		String password = users.get().getPassword("foo");

		assertEquals(e.getPassword(), password);
	}

	@Test
	@GuiceJPATestRunner.Rollback
	public void testSign() throws Exception {
		String cnonce = "5343a3ce73gi79bf437e";
		int timestamp = 1348922421;
		String username = "foo";
		String hmac = "6272b3ac2866224a3058d051d56e130a";
		String password = md5("password");

		assertEquals(hmac, users.get().sign(password, cnonce, timestamp, username));
	}

	private String md5(String message) throws Exception {
		MessageDigest md = MessageDigest.getInstance("MD5");
		return DatatypeConverter.printHexBinary(md.digest(message.getBytes("CP1252"))).toLowerCase();
	}

	private User createFooUser() throws Exception {
		User foo = new User("foo", md5("password"), "Foo Bar", "foo@email.com");
		foo.setGrad(true);
		return foo;
	}

	private void createGraduateUser(User e) {
		users.get().createGraduateUser(e.getUsername(), e.getPassword(), e.getRealName(), e.getEmail());
	}

	private void assertUsersEquals(User expected, User actual) {
		assertNotSame(expected, actual);
		assertNotNull(actual);
		assertEquals(expected.getUsername(), actual.getUsername());
		assertEquals(expected.getPassword(), actual.getPassword());
		assertEquals(expected.getRealName(), actual.getRealName());
		assertEquals(expected.getEmail(), actual.getEmail());
		assertEquals(expected.getBalance(), actual.getBalance());
		assertEquals(expected.isAdmin(), actual.isAdmin());
		assertEquals(expected.isGrad(), actual.isGrad());
		assertEquals(expected.getSponsor(), actual.getSponsor());
		assertEquals(expected.isEnabled(), actual.isEnabled());
		assertEquals(expected.isInterfridge(), actual.isInterfridge());
		assertEquals(expected.getInterfridgePassword(), actual.getInterfridgePassword());
		assertEquals(expected.getInterfridgeEndpoint(), actual.getInterfridgeEndpoint());
		assertEquals(expected.getFridgeServerEndpoint(), actual.getFridgeServerEndpoint());
	}
}
