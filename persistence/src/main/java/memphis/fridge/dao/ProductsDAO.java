package memphis.fridge.dao;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import memphis.fridge.domain.Product;
import memphis.fridge.domain.ProductImage;
import memphis.fridge.exceptions.InsufficientStockException;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 30/09/12
 */
public class ProductsDAO {

	@Inject
	Provider<EntityManager> em;

	public Product findProduct(String name) {
		return em.get().find(Product.class, name);
	}

	public void consumeProduct(Product product, int amount) {
		product = findProduct(product.getProductCode());
		int stock = product.getInStock();
		if (stock < amount) throw new InsufficientStockException();
		product.setInStock(stock - amount);
		em.get().merge(product);
	}

	public List<Product> getEnabledProducts() {
		TypedQuery<Product> q = em.get().createNamedQuery("Products.findEnabled", Product.class);
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

	public byte[] getImage(Product product) {
		ProductImage image = em.get().find(ProductImage.class, product.getProductCode());
		if (image == null) return null;
		return image.getImage();
	}

	public void storeImage(Product product, byte[] data) {
		ProductImage image = em.get().find(ProductImage.class, product.getProductCode());
		if (image != null) {
			em.get().remove(image);
		}
		image = new ProductImage(product, data);
		em.get().persist(image);
	}
}
