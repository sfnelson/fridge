package memphis.fridge.test.data;

import javax.persistence.EntityManager;

import memphis.fridge.test.persistence.TestDataProvider;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 15/10/12
 */
public class ProductsTable implements TestDataProvider {

	@Override
	public void injectData(EntityManager em) {
		new Coke().injectData(em);
		new Cookie().injectData(em);
	}
}
