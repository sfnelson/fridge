package memphis.fridge.data;

import java.math.BigDecimal;

import javax.persistence.EntityManager;
import memphis.fridge.domain.Product;
import memphis.fridge.domain.ProductCategory;
import memphis.fridge.test.persistence.TestDataProvider;

import static memphis.fridge.utils.CurrencyUtils.fromCents;
import static memphis.fridge.utils.CurrencyUtils.fromPercent;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 15/10/12
 */
public class Products implements TestDataProvider {

	public static class Drinks implements TestDataProvider {
		public static final int ID = 1;
		public static final int SEQ = 1;
		public static final String TITLE = "Drinks";

		public static ProductCategory create() {
			return new ProductCategory(ID, SEQ, TITLE);
		}

		@Override
		public void injectData(EntityManager em) {
			em.persist(create());
		}
	}

	public static class Coke implements TestDataProvider {
		public static final String CODE = "CC";
		public static final String DESC = "Coke";
		public static final BigDecimal COST = fromCents(91);
		public static final BigDecimal MARKUP = fromPercent(10);

		public static Product create(ProductCategory pc) {
			return new Product(CODE, DESC, COST, MARKUP, 20, 0, pc);
		}

		@Override
		public void injectData(EntityManager em) {
			ProductCategory drinks = Drinks.create();
			em.persist(drinks);
			em.persist(Coke.create(drinks));
		}
	}

	public static class Food implements TestDataProvider {
		public static final int ID = 2;
		public static final int SEQ = 2;
		public static final String TITLE = "Food";

		public static ProductCategory create() {
			return new ProductCategory(ID, SEQ, TITLE);
		}

		@Override
		public void injectData(EntityManager em) {
			em.persist(create());
		}
	}

	public static class Cookie implements TestDataProvider {
		public static final String CODE = "CK";
		public static final String DESC = "Cookie";
		public static final BigDecimal COST = fromCents(200);
		public static final BigDecimal MARKUP = fromPercent(50);

		public static Product create(ProductCategory food) {
			return new Product(CODE, DESC, COST, MARKUP, 10, 0, food);
		}

		@Override
		public void injectData(EntityManager em) {
			ProductCategory food = Food.create();
			em.persist(food);
			em.persist(Cookie.create(food));
		}
	}

	@Override
	public void injectData(EntityManager em) {
		new Coke().injectData(em);
		new Cookie().injectData(em);
	}
}
