package memphis.fridge.server.services;

import java.math.BigDecimal;
import java.util.List;

import com.google.inject.persist.Transactional;
import javax.inject.Inject;
import memphis.fridge.dao.*;
import memphis.fridge.domain.Product;
import memphis.fridge.domain.User;
import memphis.fridge.exceptions.InsufficientStockException;
import memphis.fridge.exceptions.InvalidProductException;
import memphis.fridge.server.io.HMACResponse;
import memphis.fridge.server.io.ResponseSerializer;
import memphis.fridge.utils.Pair;

import static memphis.fridge.utils.CurrencyUtils.*;

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

	@Inject
	FridgeDAO fridge;

	@Inject
	CreditLogDAO creditLog;

	@Transactional
	public HMACResponse purchase(String snonce, String username, List<Pair<String, Integer>> items, String hmac) {
		users.validateHMAC(username, hmac, snonce, username, items);

		User user = users.retrieveUser(username);

		/**
		 * This is counter-intuitive, but the graduate discount is actually a markup on undergrads.
		 */
		BigDecimal tax = user.isGrad() ? BigDecimal.ZERO : fridge.getGraduateDiscount();
		BigDecimal totalCost = BigDecimal.ZERO;

		/**
		 * For each item in the purchase list:
		 *  * check that the product exists
		 *  * check that sufficient stock is available
		 *  * remove stock
		 *  * create a purchase record
		 *  * add cost to cumulative total
		 */
		for (Pair<String, Integer> item : items) {
			Product product = products.findProduct(item.getKey());
			int count = item.getValue();
			if (product == null) throw new InvalidProductException(item.getKey());
			if (product.getInStock() < count) throw new InsufficientStockException();
			products.consumeProduct(product, count);

			BigDecimal cost = calculateCost(product, count);
			BigDecimal surplus = calculateMarkup(product, count, tax);

			purchases.createPurchase(user, product, count, cost, surplus);

			totalCost = add(totalCost, cost, surplus);
		}

		/**
		 * Remove the funds from the user's account if possible. Throws an exception if
		 * the user does not have the request funds or cannot go sufficiently negative.
		 */
		users.removeFunds(user, totalCost);

		/* Create a transaction record for this purchase */
		creditLog.createPurchase(user, totalCost);

		return new PurchaseResponse(username,
				snonce,
				toCents(users.retrieveUser(username).getBalance()),
				toCents(totalCost));
	}

	private static BigDecimal add(BigDecimal... toAdd) {
		BigDecimal sum = BigDecimal.ZERO;
		for (BigDecimal v : toAdd) {
			sum = sum.add(v);
		}
		return sum;
	}

	private class PurchaseResponse extends HMACResponse {
		int balance;
		int order_total;

		PurchaseResponse(String username, String snonce, int balance, int order_total) {
			super(users, username, snonce, balance, order_total);
			this.balance = balance;
			this.order_total = order_total;
		}

		@Override
		protected void visitParams(ResponseSerializer.ObjectSerializer visitor) {
			visitor.visitInteger("balance", balance);
			visitor.visitInteger("order_total", order_total);
		}
	}
}
