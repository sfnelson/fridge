package memphis.fridge.dao;

import java.math.BigDecimal;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.persistence.EntityManager;
import memphis.fridge.domain.CreditLogEntry;
import memphis.fridge.domain.User;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 6/10/12
 */
public class CreditLogDAO {

	@Inject
	Provider<EntityManager> em;

	public void createPurchase(User user, BigDecimal amount) {
		em.get().persist(CreditLogEntry.createPurchase(user, amount));
	}
}
