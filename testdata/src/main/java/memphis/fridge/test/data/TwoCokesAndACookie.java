package memphis.fridge.test.data;

import java.math.BigDecimal;

import static memphis.fridge.utils.CurrencyUtils.markup;

/**
* @author stephen
*/
public class TwoCokesAndACookie {
    public static class Cokes {
        public static final int NUM = 2;
        public static final BigDecimal UNIT_BASE = Coke.BASE;
        public static final BigDecimal UNIT_TAX = Coke.TAX_RATE;
        public static BigDecimal baseCost() {
            return BigDecimal.valueOf(NUM).multiply(UNIT_BASE);
        }
        public static BigDecimal taxCost(boolean isGrad) {
            return markup(baseCost(), isGrad ? UNIT_TAX : UNIT_TAX.add(Utils.GRAD_TAX));
        }
        public static BigDecimal total(boolean isGrad) {
            return baseCost().add(taxCost(isGrad));
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
            return markup(baseCost(), isGrad ? UNIT_TAX : UNIT_TAX.add(Utils.GRAD_TAX));
        }
        public static BigDecimal total(boolean isGrad) {
            return baseCost().add(taxCost(isGrad));
        }
    }

    public static BigDecimal baseCost() {
        return Utils.add(Cokes.baseCost(), Cookies.baseCost());
    }

    public static BigDecimal taxCost(boolean isGrad) {
        return Utils.add(Cokes.taxCost(isGrad), Cookies.taxCost(isGrad));
    }

    public static BigDecimal total(boolean isGrad) {
        return Utils.add(Cokes.total(isGrad), Cookies.total(isGrad));
    }
}
