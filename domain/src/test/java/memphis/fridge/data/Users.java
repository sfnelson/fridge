package memphis.fridge.data;

import javax.persistence.EntityManager;
import memphis.fridge.domain.User;
import memphis.fridge.test.persistence.TestDataProvider;

import static memphis.fridge.utils.CryptUtils.md5;
import static memphis.fridge.utils.CurrencyUtils.fromCents;
import static org.junit.Assert.*;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 14/10/12
 */
public class Users {

	public static class Admin implements TestDataProvider {
		public static final String NAME = "admin";
		public static final String PASS = "pa55word";
		public static final String REAL = "Admin User";
		public static final String EMAIL = "admin@domain.com";
		public static final int BALANCE = 5000;

		@Override
		public void injectData(EntityManager em) {
			em.persist(create());
		}

		public static User create() {
			try {
				User user = new User(NAME, md5(PASS), REAL, EMAIL);
				user.setBalance(fromCents(BALANCE));
				user.setAdmin(true);
				user.setGrad(true);
				return user;
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
		}
	}

	public static class Graduate implements TestDataProvider {
		public static final String NAME = "graduate";
		public static final String PASS = "passw0rd";
		public static final String REAL = "Graduate User";
		public static final String EMAIL = "graduate@domain.com";
		public static final int BALANCE = 500;

		@Override
		public void injectData(EntityManager em) {
			em.persist(create());
		}

		public static User create() {
			try {
				User user = new User(NAME, md5(PASS), REAL, EMAIL);
				user.setBalance(fromCents(BALANCE));
				user.setGrad(true);
				return user;
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
		}
	}

	public static class Undergrad implements TestDataProvider {
		public static final String NAME = "undergrad";
		public static final String PASS = "p4ssword";
		public static final String REAL = "Undergrad User";
		public static final String EMAIL = "undergrad@domain.com";
		public static final int BALANCE = 1000;

		@Override
		public void injectData(EntityManager em) {
			em.persist(create());
		}

		public static User create() {
			try {
				User user = new User(NAME, md5(PASS), REAL, EMAIL);
				user.setBalance(fromCents(BALANCE));
				return user;
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
		}
	}

	public static class CreateThreeUsers implements TestDataProvider {
		@Override
		public void injectData(EntityManager em) {
			em.persist(Admin.create());
			em.persist(Graduate.create());
			em.persist(Undergrad.create());
		}
	}

	public static void assertUsersEquals(User expected, User actual) {
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
