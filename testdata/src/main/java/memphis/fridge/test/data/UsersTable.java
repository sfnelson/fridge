package memphis.fridge.test.data;

import javax.persistence.EntityManager;
import memphis.fridge.domain.User;
import memphis.fridge.test.persistence.TestDataProvider;

import static org.junit.Assert.*;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 14/10/12
 */
public class UsersTable {

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
