package memphis.fridge.dao;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import memphis.fridge.domain.Product;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 30/09/12
 */
public class ProductsDAO {

	@Inject
	private EntityManager em;

	public Product findProduct(String name) {
		return em.find(Product.class, name);
	}

	public void removeProduct(Product product, int amount) {
		throw new UnsupportedOperationException();
	}

}
