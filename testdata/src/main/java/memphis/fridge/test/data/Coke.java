package memphis.fridge.test.data;

import memphis.fridge.domain.Product;
import memphis.fridge.domain.ProductCategory;
import memphis.fridge.test.persistence.TestDataProvider;

import javax.persistence.EntityManager;
import java.math.BigDecimal;

import static memphis.fridge.utils.CurrencyUtils.*;

/**
 * @author stephen
 */
public class Coke implements TestDataProvider {
    public static final String CODE = "CC";
    public static final String DESC = "Coke";
    public static final BigDecimal BASE = fromCents(100);
    public static final BigDecimal TAX_RATE = fromPercent(20);
    public static final BigDecimal TAX_UNIT = markup(BASE, TAX_RATE);
    public static final int STOCK = 18;

    public static Product create() {
        return create(Drinks.create());
    }

    public static Product create(ProductCategory category) {
        Product coke = new Product(CODE, DESC, BASE, TAX_RATE, category);
        coke.setInStock(STOCK);
        coke.setEnabled(true);
        return coke;
    }

    public static BigDecimal tax(boolean isGrad) {
        return markup(BASE, isGrad ? TAX_RATE : TAX_RATE.add(Utils.GRAD_TAX));
    }

    public static BigDecimal total(boolean isGrad) {
        return BASE.add(tax(isGrad));
    }

    @Override
    public void injectData(EntityManager em) {
        ProductCategory drinks = Drinks.create();
        em.persist(drinks);
        em.persist(Coke.create(drinks));
    }
}