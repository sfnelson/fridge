package memphis.fridge.dao;

import java.math.BigDecimal;
import java.util.Date;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.persistence.EntityManager;
import memphis.fridge.domain.Product;
import memphis.fridge.domain.Purchase;
import memphis.fridge.domain.User;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 30/09/12
 */
public class PurchaseDAO {

	@Inject
	Provider<EntityManager> em;

	@RequireTransaction
	public Purchase createPurchase(User user, Product product, int count, BigDecimal cost, BigDecimal surplus) {
		Purchase p = new Purchase(user, product, new Date(), count, cost, surplus);
		em.get().persist(p);
		return p;
	}
}
