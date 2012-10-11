package memphis.fridge.server.services;

import com.google.inject.persist.Transactional;
import javax.inject.Inject;
import memphis.fridge.dao.ProductCategoryDAO;
import memphis.fridge.dao.ProductsDAO;
import memphis.fridge.dao.UserDAO;
import memphis.fridge.domain.Product;
import memphis.fridge.domain.ProductCategory;
import memphis.fridge.server.ioc.RequireAdmin;
import memphis.fridge.utils.CurrencyUtils;

public class Products {

	@Inject
	UserDAO userDAO;

	@Inject
	ProductsDAO productsDAO;

	@Inject
	ProductCategoryDAO categoryDAO;

	@Transactional
	@RequireAdmin
	public void addProduct(String code, String description, int categoryId, int initialStock, int minimumStock,
						   int cost, int markup) {

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
	}

	@Transactional
	@RequireAdmin
	public void updateProduct(String code, String description, int categoryId, int minimumStock, int cost, int markup,
							  boolean enabled) {

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
	}

	@Transactional
	@RequireAdmin
	public void storeProductImage(String code, byte[] image) {
		Product product = productsDAO.findProduct(code);
		productsDAO.storeImage(product, image);
	}

	public byte[] getProductImage(String code) {
		Product product = productsDAO.findProduct(code);
		return productsDAO.getImage(product);
	}
}
