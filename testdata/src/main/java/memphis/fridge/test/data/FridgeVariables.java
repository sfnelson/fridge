package memphis.fridge.test.data;

import javax.persistence.EntityManager;
import memphis.fridge.domain.NumericalVariable;
import memphis.fridge.test.persistence.TestDataProvider;

import static memphis.fridge.utils.CurrencyUtils.fromCents;
import static memphis.fridge.utils.CurrencyUtils.fromPercent;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 15/10/12
 */
public class FridgeVariables implements TestDataProvider {

	@Override
	public void injectData(EntityManager em) {
		em.persist(new NumericalVariable("graduate_discount", fromPercent(50)));
		em.persist(new NumericalVariable("minimum_user_balance", fromCents(-500)));
		em.persist(new NumericalVariable("minimum_administrator_balance", fromCents(-5000)));
	}
}
