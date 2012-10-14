package memphis.fridge.dao;

import java.math.BigDecimal;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.persistence.EntityManager;
import javax.persistence.Query;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 2/10/12
 */
public class FridgeDAO {

	@Inject
	Provider<EntityManager> em;

	public BigDecimal getGraduateDiscount() {
		Query q = em.get().createNativeQuery("SELECT value FROM numerical_variables WHERE variable = 'graduate_discount'");
		BigDecimal result = (BigDecimal) q.getSingleResult();
		result = result.setScale(1);
		return result;
	}

	public BigDecimal getMinimumUserBalance() {
		Query q = em.get().createNativeQuery("SELECT value FROM numerical_variables WHERE variable = 'minimum_user_balance'");
		BigDecimal result = (BigDecimal) q.getSingleResult();
		result = result.setScale(2);
		return result;
	}

	public BigDecimal getMinimumAdministratorBalance() {
		Query q = em.get().createNativeQuery("SELECT value FROM numerical_variables WHERE variable = 'minimum_administrator_balance'");
		BigDecimal result = (BigDecimal) q.getSingleResult();
		result = result.setScale(2);
		return result;
	}
}
