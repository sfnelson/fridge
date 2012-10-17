package memphis.fridge.server.services;

import com.google.common.annotations.VisibleForTesting;
import com.google.inject.persist.Transactional;
import javax.inject.Inject;

import memphis.fridge.dao.FridgeDAO;
import memphis.fridge.dao.ProductCategoryDAO;
import memphis.fridge.dao.ProductsDAO;
import memphis.fridge.domain.Product;
import memphis.fridge.domain.ProductCategory;
import memphis.fridge.protocol.Messages;
import memphis.fridge.server.ioc.RequireAdmin;
import memphis.fridge.server.ioc.RequireAuthenticated;
import memphis.fridge.server.ioc.SessionState;
import memphis.fridge.utils.CurrencyUtils;

import java.math.BigDecimal;
import java.util.List;

import static memphis.fridge.utils.CurrencyUtils.markup;
import static memphis.fridge.utils.CurrencyUtils.toCents;

public class Products {

    @VisibleForTesting
    @Inject
    SessionState session;

    @VisibleForTesting
    @Inject
    FridgeDAO fridgeDAO;

	@Inject
	ProductsDAO productsDAO;

	@Inject
	ProductCategoryDAO categoryDAO;

    @RequireAuthenticated
    public Messages.StockResponse getProductsAuthenticated() {
        BigDecimal tax = session.getUser().isGrad() ? BigDecimal.ZERO : fridgeDAO.getGraduateDiscount();
        return getProducts(tax);
    }

    public Messages.StockResponse getProducts() {
        return getProducts(fridgeDAO.getGraduateDiscount());
    }

    private Messages.StockResponse getProducts(BigDecimal tax) {
        List<Product> products = this.productsDAO.getEnabledProducts();
        Messages.StockResponse.Builder response = Messages.StockResponse.newBuilder();
        for (Product p : products) {
            int price = toCents(p.getCost().add(markup(p.getCost(), tax.add(p.getMarkup()))));
            response.addStockBuilder()
                    .setProductCode(p.getProductCode())
                    .setDescription(p.getDescription())
                    .setInStock(p.getInStock())
                    .setPrice(price)
                    .setCategory(p.getCategory().getTitle())
                    .setCategoryOrder(p.getCategory().getDisplaySequence());
        }
        return response.build();
    }

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
