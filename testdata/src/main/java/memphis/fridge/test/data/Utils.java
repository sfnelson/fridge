package memphis.fridge.test.data;

import java.math.BigDecimal;

import static memphis.fridge.utils.CurrencyUtils.*;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 6/10/12
 */
public class Utils {

    public static final String USERNAME = "{USERNAME}";
    public static final String SNONCE = "{SNONCE}";
    public static final String HMAC = "{HMAC}";

    public static final BigDecimal GRAD_TAX = fromPercent(10);

    public static BigDecimal add(BigDecimal... costs) {
        BigDecimal total = BigDecimal.ZERO;
        for (BigDecimal c : costs) {
            total = total.add(c);
        }
        return total;
    }
}
