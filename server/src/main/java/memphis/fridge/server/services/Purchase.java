package memphis.fridge.server.services;

import java.math.BigDecimal;

import com.google.inject.persist.Transactional;
import javax.inject.Inject;
import memphis.fridge.dao.*;
import memphis.fridge.domain.Product;
import memphis.fridge.domain.User;
import memphis.fridge.exceptions.InsufficientStockException;
import memphis.fridge.exceptions.InvalidProductException;
import memphis.fridge.protocol.Messages;
import memphis.fridge.server.ioc.RequireAuthenticated;
import memphis.fridge.server.ioc.SessionState;

import static memphis.fridge.utils.CurrencyUtils.*;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 30/09/12
 */
public class Purchase {

	@Inject
	SessionState session;

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
	@RequireAuthenticated
	public Messages.TransactionResponse purchase(Messages.PurchaseRequest request) {
		User user = session.getUser();

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
		for (Messages.PurchaseRequest.Order item : request.getOrdersList()) {
			Product product = products.findProduct(item.getCode());
			int count = item.getQuantity();
			if (product == null) throw new InvalidProductException(item.getCode());
			if (product.getInStock() < count) throw new InsufficientStockException();
			products.consumeProduct(product, count);

			BigDecimal cost = calculateCost(product, count);
			BigDecimal surplus = calculateMarkup(product, count, tax);

			purchases.createPurchase(user, product, count, cost, surplus);

			totalCost = sum(totalCost, cost, surplus);
		}

		/**
		 * Remove the funds from the user's account if possible. Throws an exception if
		 * the user does not have the request funds or cannot go sufficiently negative.
		 */
		users.removeFunds(user, totalCost);

		/* Create a transaction record for this purchase */
		creditLog.createPurchase(user, totalCost);

		user = users.retrieveUser(user.getUsername());

		return Messages.TransactionResponse.newBuilder()
				.setBalance(toCents(user.getBalance()))
				.setCost(toCents(totalCost)).build();
	}

	private static BigDecimal sum(BigDecimal... toAdd) {
		BigDecimal sum = BigDecimal.ZERO;
		for (BigDecimal v : toAdd) {
			sum = sum.add(v);
		}
		return sum;
	}
}
