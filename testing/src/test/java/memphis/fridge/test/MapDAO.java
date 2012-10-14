package memphis.fridge.test;

import javax.inject.Inject;
import javax.persistence.EntityManager;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 14/10/12
 */
public class MapDAO {

	@Inject
	EntityManager em;

	public String get(Object key) {
		if (key instanceof String) {
			MapEntry entity = em.find(MapEntry.class, key);
			if (entity == null) return null;
			return entity.getValue();
		}
		return null;
	}

	public String put(String key, String value) {
		MapEntry entity = em.find(MapEntry.class, key);
		if (entity == null) {
			entity = new MapEntry(key, value);
			em.persist(entity);
			return null;
		} else {
			String oldValue = entity.getValue();
			entity.setValue(value);
			return oldValue;
		}
	}
}
