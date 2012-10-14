package memphis.fridge.test;

import com.google.inject.persist.jpa.JpaPersistModule;
import javax.inject.Inject;
import memphis.fridge.test.persistence.WithTestData;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 14/10/12
 */
@TestModule(value = JpaPersistModule.class, args = "memphis.fridge.test")
public class MapTest extends GuiceJPATest {

	@Inject
	MapDAO dao;

	@Test
	public void testGetNull() throws Exception {
		assertNull(dao.get("a"));
	}

	@Test
	@WithTestData(Data.class)
	public void testGetProvided() throws Exception {
		assertEquals("apple", dao.get("a"));
		assertEquals("banana", dao.get("b"));
		assertEquals("carrot", dao.get("c"));
	}
}
