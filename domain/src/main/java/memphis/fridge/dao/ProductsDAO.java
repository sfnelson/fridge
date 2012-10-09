package memphis.fridge.dao;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import memphis.fridge.domain.Product;
import memphis.fridge.exceptions.InsufficientStockException;

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

	public void consumeProduct(Product product, int amount) {
		product = findProduct(product.getProductCode());
		int stock = product.getInStock();
		if (stock < amount) throw new InsufficientStockException();
		product.setInStock(stock - amount);
		em.merge(product);
	}

	public List<Product> getEnabledProducts() {
		TypedQuery<Product> q = em.createNamedQuery("Products.findEnabled", Product.class);
		return q.getResultList();
	}

    public void add(Product product) {
        throw new UnsupportedOperationException();
    }

    public void save(Product product) {
        throw new UnsupportedOperationException();
    }

    public void checkProductNotExists(String code) {
        throw new UnsupportedOperationException();
    }
}
