package memphis.fridge.dao;

import java.math.BigDecimal;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 2/10/12
 */
public class FridgeDAO {

	@Inject
	EntityManager em;

	public BigDecimal getGraduateDiscount() {
		Query q = em.createNativeQuery("SELECT value FROM numerical_variables WHERE variable = 'graduate_discount'");
		BigDecimal result = (BigDecimal) q.getSingleResult();
		result = result.setScale(1);
		return result;
	}
}
