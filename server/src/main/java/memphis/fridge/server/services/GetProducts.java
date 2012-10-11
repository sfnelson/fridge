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
import memphis.fridge.protocol.Messages;

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

	public Messages.StockResponse getProducts(@Nullable String username) {
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
}
