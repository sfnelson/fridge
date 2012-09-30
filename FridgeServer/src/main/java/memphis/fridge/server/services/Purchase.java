package memphis.fridge.server.services;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Maps;
import com.google.inject.persist.Transactional;
import javax.inject.Inject;
import memphis.fridge.dao.ProductsDAO;
import memphis.fridge.dao.PurchaseDAO;
import memphis.fridge.dao.UserDAO;
import memphis.fridge.domain.Product;
import memphis.fridge.exceptions.InvalidProductException;
import memphis.fridge.server.io.Response;
import memphis.fridge.utils.Pair;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 30/09/12
 */
public class Purchase {

	@Inject
	UserDAO users;

	@Inject
	ProductsDAO products;

	@Inject
	PurchaseDAO purchases;

	@Transactional
	public Response purchase(String snonce, String user, List<Pair<String, Integer>> items, String hmac) {
		users.validateHMAC(user, hmac, snonce, user, items);

		Map<String, Product> cache = Maps.newHashMap();

		BigDecimal totalCost = BigDecimal.ZERO;
		for (Pair<String, Integer> item : items) {
			Product p = products.findProduct(item.getKey());
			if (p == null) throw new InvalidProductException();
			cache.put(item.getKey(), p);
			totalCost = totalCost.add(
					cost(p.getCost(), p.getMarkup(), BigDecimal.valueOf(item.getValue()))
			);
		}

		users.checkSufficientBalance(user, totalCost);

		for (Pair<String, Integer> item : items) {
			Product p = products.findProduct(item.getKey());
			if (p == null) throw new InvalidProductException();
			cache.put(item.getKey(), p);
			totalCost = totalCost.add(
					cost(p.getCost(), p.getMarkup(), BigDecimal.valueOf(item.getValue()))
			);
		}

		return null;
	}

	@VisibleForTesting
	BigDecimal cost(BigDecimal cost, BigDecimal markup, BigDecimal num) {
		return cost.add(cost.multiply(markup.divide(BigDecimal.valueOf(100))))
				.multiply(num).setScale(2);
	}
}
