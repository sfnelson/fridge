package memphis.fridge.test.persistence;

import javax.persistence.EntityManager;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 14/10/12
 */
public interface TestDataProvider {

	void injectData(EntityManager em);

}
