package memphis.fridge.server.services;

import com.google.inject.persist.Transactional;
import javax.inject.Inject;
import memphis.fridge.dao.ProductCategoryDAO;
import memphis.fridge.dao.ProductsDAO;
import memphis.fridge.dao.UserDAO;
import memphis.fridge.domain.Product;
import memphis.fridge.domain.ProductCategory;
import memphis.fridge.server.io.Response;
import memphis.fridge.server.io.ResponseSerializer;
import memphis.fridge.utils.CurrencyUtils;

public class Products {

	@Inject
	UserDAO userDAO;

	@Inject
	ProductsDAO productsDAO;

	@Inject
	ProductCategoryDAO categoryDAO;

	@Transactional
	public Response addProduct(String snonce, String user, String code, String description, int categoryId, int initialStock,
							   int minimumStock, int cost, int markup, String hmac) {
		userDAO.validateHMAC(user, hmac, snonce, code, description, categoryId, initialStock, minimumStock, cost, markup);
		userDAO.checkAdmin(user);

		productsDAO.checkProductNotExists(code);

		ProductCategory category = categoryDAO.findCategory(categoryId);

		Product product = new Product(
				code,
				description,
				CurrencyUtils.fromCents(cost),
				CurrencyUtils.fromPercent(markup),
				initialStock,
				minimumStock,
				category);

		productsDAO.add(product);

		return new Response() {
			public void visit(ResponseSerializer visitor) {
			}
		};
	}

	@Transactional
	public Response updateProduct(String snonce, String user, String code, String description, int categoryId, int minimumStock, int cost, int markup,
								  boolean enabled, String hmac) {
		userDAO.validateHMAC(user, hmac, snonce, code, description, categoryId, minimumStock, cost, markup, enabled);
		userDAO.checkAdmin(user);

		Product product = productsDAO.findProduct(code);
		product.setDescription(description);
		product.setCost(CurrencyUtils.fromCents(cost));
		product.setMarkup(CurrencyUtils.fromPercent(markup));
		product.setEnabled(enabled);
		product.setStockLowMark(minimumStock);

		if (product.getCategory().getId() != categoryId) {
			ProductCategory category = categoryDAO.findCategory(categoryId);
			product.setCategory(category);
		}

		productsDAO.save(product);

		return new Response() {
			public void visit(ResponseSerializer visitor) {
			}
		};
	}

	@Transactional
	public Response storeProductImage(String code, byte[] image) {
		// TODO validate user and hmac as admin

		Product product = productsDAO.findProduct(code);
		productsDAO.storeImage(product, image);

		return new Response() {
			public void visit(ResponseSerializer visitor) {
			}
		};
	}

	public byte[] getProductImage(String code) {
		Product product = productsDAO.findProduct(code);
		return productsDAO.getImage(product);
	}
}
