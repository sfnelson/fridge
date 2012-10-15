package memphis.fridge.server;

import java.math.BigDecimal;
import java.util.Date;

import memphis.fridge.dao.ProductsDAO;
import memphis.fridge.dao.PurchaseDAO;
import memphis.fridge.domain.Product;
import memphis.fridge.domain.ProductCategory;
import memphis.fridge.domain.Purchase;
import memphis.fridge.domain.User;
import memphis.fridge.protocol.Messages;
import memphis.fridge.server.services.Products;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;
import static memphis.fridge.utils.CurrencyUtils.*;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 6/10/12
 */
public class TestingData {

    public static final String USERNAME = "{USERNAME}";
    public static final String SNONCE = "{SNONCE}";
    public static final String HMAC = "{HMAC}";

    public static final BigDecimal GRAD_TAX = fromPercent(10);

    public static class Drinks {
        public static final int ID = 0;
        public static final int ORDER = 1;
        public static final String TITLE = "Drinks";

        public static ProductCategory create() {
            return new ProductCategory(ID, ORDER, TITLE);
        }
    }

    public static class Snacks {
        public static final int ID = 1;
        public static final int ORDER = 2;
        public static final String TITLE = "Snacks";

        public static ProductCategory create() {
            return new ProductCategory(ID, ORDER, TITLE);
        }
    }

    public static class Chocolate {
        public static final int ID = 2;
        public static final int ORDER = 3;
        public static final String TITLE = "Chocolate";

        public static ProductCategory create() {
            return new ProductCategory(ID, ORDER, TITLE);
        }
    }

    public static class Coke {
        public static final String CODE = "CC";
        public static final String DESC = "Coke";
        public static final BigDecimal BASE = fromCents(100);
        public static final BigDecimal TAX_RATE = fromPercent(20);
        public static final BigDecimal TAX_UNIT = markup(BASE, TAX_RATE);
        public static final int STOCK = 18;

        public static Product create() {
            Product coke = new Product(CODE, DESC, BASE, TAX_RATE, Drinks.create());
            coke.setInStock(STOCK);
            coke.setEnabled(true);
            return coke;
        }

        public static void add(Products service) {
            service.addProduct(CODE, DESC, Drinks.ID, STOCK, 0, toCents(BASE), toPercent(TAX_RATE));
        }

        public static void update(Products service, boolean enabled) {
            service.updateProduct(CODE, DESC, Drinks.ID, 0, toCents(BASE), toPercent(TAX_RATE), enabled);
        }

        public static BigDecimal tax(boolean isGrad) {
            return markup(BASE, isGrad ? TAX_RATE : TAX_RATE.add(GRAD_TAX));
        }

        public static BigDecimal total(boolean isGrad) {
            return BASE.add(tax(isGrad));
        }
    }

    public static class Cookie {
        public static final String CODE = "CK";
        public static final String DESC = "Cookie";
        public static final BigDecimal BASE = fromCents(200);
        public static final BigDecimal TAX_RATE = fromPercent(20);
        public static final BigDecimal TAX_UNIT = markup(BASE, TAX_RATE);
        public static final int STOCK = 9;

        public static Product create() {
            Product cookie = new Product(CODE, DESC, BASE, TAX_RATE, Snacks.create());
            cookie.setInStock(STOCK);
            cookie.setEnabled(true);
            return cookie;
        }

        public static void add(Products service) {
            service.addProduct(CODE, DESC, Snacks.ID, STOCK, 0, toCents(BASE), toPercent(TAX_RATE));
        }

        public static void update(Products service, boolean enabled) {
            service.updateProduct(CODE, DESC, Snacks.ID, 0, toCents(BASE), toPercent(TAX_RATE), enabled);
        }

        public static BigDecimal tax(boolean isGrad) {
            return markup(BASE, isGrad ? TAX_RATE : TAX_RATE.add(GRAD_TAX));
        }

        public static BigDecimal total(boolean isGrad) {
            return BASE.add(tax(isGrad));
        }
    }

    public static class BasicOrder {
        public static class Cokes {
            public static final int NUM = 2;
            public static final BigDecimal UNIT_BASE = Coke.BASE;
            public static final BigDecimal UNIT_TAX = Coke.TAX_RATE;
            public static BigDecimal baseCost() {
                return BigDecimal.valueOf(NUM).multiply(UNIT_BASE);
            }
            public static BigDecimal taxCost(boolean isGrad) {
                return markup(baseCost(), isGrad ? UNIT_TAX : UNIT_TAX.add(GRAD_TAX));
            }
            public static BigDecimal total(boolean isGrad) {
                return baseCost().add(taxCost(isGrad));
            }
            public static Purchase createPurchase(PurchaseDAO p, User user, boolean isGrad) {
                return p.createPurchase(user, Coke.create(), NUM, baseCost(), taxCost(isGrad));
            }
            public static void verifyPurchase(ProductsDAO products, PurchaseDAO purchases, User user) {
                verify(products).consumeProduct(Coke.create(), NUM);
                verify(purchases).createPurchase(user, Coke.create(), NUM, baseCost(), taxCost(user.isGrad()));
            }
        }

        public static class Cookies {
            public static final int NUM = 1;
            public static final BigDecimal UNIT_BASE = Cookie.BASE;
            public static final BigDecimal UNIT_TAX = Cookie.TAX_RATE;
            public static BigDecimal baseCost() {
                return BigDecimal.valueOf(NUM).multiply(UNIT_BASE);
            }
            public static BigDecimal taxCost(boolean isGrad) {
                return markup(baseCost(), isGrad ? UNIT_TAX : UNIT_TAX.add(GRAD_TAX));
            }
            public static BigDecimal total(boolean isGrad) {
                return baseCost().add(taxCost(isGrad));
            }
            public static Purchase createPurchase(PurchaseDAO p, User user, boolean isGrad) {
                return p.createPurchase(user, Coke.create(), NUM, baseCost(), taxCost(isGrad));
            }
            public static void verifyPurchase(ProductsDAO products, PurchaseDAO purchases, User user) {
                verify(products).consumeProduct(Cookie.create(), NUM);
                verify(purchases).createPurchase(user, Cookie.create(), NUM, baseCost(), taxCost(user.isGrad()));
            }
        }

        public static BigDecimal baseCost() {
            return add(Cokes.baseCost(), Cookies.baseCost());
        }

        public static BigDecimal taxCost(boolean isGrad) {
            return add(Cokes.taxCost(isGrad), Cookies.taxCost(isGrad));
        }

        public static BigDecimal total(boolean isGrad) {
            return add(Cokes.total(isGrad), Cookies.total(isGrad));
        }

        public static Messages.PurchaseRequest order() {
            Messages.PurchaseRequest.Builder rq = Messages.PurchaseRequest.newBuilder();
            rq.addOrdersBuilder().setCode(Coke.CODE).setQuantity(Cokes.NUM);
            rq.addOrdersBuilder().setCode(Cookie.CODE).setQuantity(Cookies.NUM);
            return rq.build();
        }
    }

    private static BigDecimal add(BigDecimal... costs) {
        BigDecimal total = BigDecimal.ZERO;
        for (BigDecimal c : costs) {
            total = total.add(c);
        }
        return total;
    }
}
