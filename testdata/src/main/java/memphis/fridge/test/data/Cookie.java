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
public class Cookie implements TestDataProvider {
    public static final String CODE = "CK";
    public static final String DESC = "Cookie";
    public static final BigDecimal BASE = fromCents(200);
    public static final BigDecimal TAX_RATE = fromPercent(50);
    public static final BigDecimal TAX_UNIT = markup(BASE, TAX_RATE);
    public static final int STOCK = 9;

    public static Product create() {
        return create(Snacks.create());
    }

    public static Product create(ProductCategory category) {
        Product cookie = new Product(CODE, DESC, BASE, TAX_RATE, category);
        cookie.setInStock(STOCK);
        cookie.setEnabled(true);
        return cookie;
    }

    public static BigDecimal tax(boolean isGrad) {
        return markup(BASE, isGrad ? TAX_RATE : TAX_RATE.add(Utils.GRAD_TAX));
    }

    public static BigDecimal total(boolean isGrad) {
        return BASE.add(tax(isGrad));
    }

    @Override
    public void injectData(EntityManager em) {
        ProductCategory food = Snacks.create();
        em.persist(food);
        em.persist(Cookie.create(food));
    }
}