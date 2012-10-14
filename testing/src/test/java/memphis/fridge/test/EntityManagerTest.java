package memphis.fridge.test;

import com.google.inject.persist.jpa.JpaPersistModule;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import memphis.fridge.test.persistence.GuiceJPAModule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 14/10/12
 */
@TestModule(value = GuiceJPAModule.class, args = "memphis.fridge.test")
public class EntityManagerTest extends GuiceJPATest {

	@Inject
	EntityManager em;

	@Test
	public void testEntityManager() throws Exception {
		MapEntry obj = new MapEntry("foo", "bar");

		em.persist(obj);

		assertEquals(obj, em.find(MapEntry.class, "foo"));
	}

	@Test
	public void testRollback() throws Exception {
		assertNull(em.find(MapEntry.class, "foo"));
	}
}
