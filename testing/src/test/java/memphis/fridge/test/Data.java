package memphis.fridge.test;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import javax.persistence.EntityManager;
import memphis.fridge.test.persistence.TestDataProvider;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 14/10/12
 */
public class Data implements TestDataProvider {

	public static final Map<String, String> DATA = ImmutableMap.of(
			"a", "apple",
			"b", "banana",
			"c", "carrot"
	);

	@Override
	public void injectData(EntityManager em) {
		for (Map.Entry<String, String> e : DATA.entrySet()) {
			em.persist(new MapEntry(e.getKey(), e.getValue()));
		}
	}
}
