package memphis.fridge.server.services;

import java.math.BigDecimal;
import java.util.List;

import com.google.common.annotations.VisibleForTesting;
import javax.annotation.Nullable;
import javax.inject.Inject;
import memphis.fridge.dao.FridgeDAO;
import memphis.fridge.dao.ProductsDAO;
import memphis.fridge.dao.UserDAO;
import memphis.fridge.domain.Product;
import memphis.fridge.domain.User;
import memphis.fridge.exceptions.FridgeException;

import static memphis.fridge.utils.CurrencyUtils.markup;
import static memphis.fridge.utils.CurrencyUtils.toCents;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 7/10/12
 */
public class GetProducts {

	@VisibleForTesting
	@Inject
	UserDAO users;

	@VisibleForTesting
	@Inject
	ProductsDAO products;

	@VisibleForTesting
	@Inject
	FridgeDAO fridge;

	public ListResponse<?> getProducts(@Nullable String username) {
		List<Product> products = this.products.getEnabledProducts();

		BigDecimal tax = fridge.getGraduateDiscount();
		if (username != null) {
			try {
				User user = users.retrieveUser(username);
				if (user.isGrad()) {
					tax = BigDecimal.ZERO;
				}
			} catch (FridgeException ex) {
			}
		}

		ProductsResponse response = new ProductsResponse();
		for (Product p : products) {
			int price = toCents(p.getCost().add(markup(p.getCost(), tax.add(p.getMarkup()))));
			response.add(new ProductResponse(p, price));
		}
		return response;
	}

	private static class ProductsResponse extends ListResponse<ProductResponse> {
		public void add(ProductResponse item) {
			super.add(item);
		}
	}

	private static class ProductResponse extends ObjectResponse {
		String product_code;
		String description;
		int in_stock;
		int price;
		String category;
		int category_order;

		ProductResponse(Product p, int price) {
			this.product_code = p.getProductCode();
			this.description = p.getDescription();
			this.in_stock = p.getInStock();
			this.price = price;
			this.category = p.getCategory().getTitle();
			this.category_order = p.getCategory().getDisplaySequence();
		}

		@Override
		public void visit(ResponseSerializer.ObjectSerializer visitor) {
			visitor.visitString("product_code", product_code);
			visitor.visitString("description", description);
			visitor.visitInteger("in_stock", in_stock);
			visitor.visitInteger("price", price);
			visitor.visitString("category", category);
			visitor.visitInteger("category_order", category_order);
		}
	}

}
