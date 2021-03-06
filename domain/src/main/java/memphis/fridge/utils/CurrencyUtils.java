package memphis.fridge.utils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import memphis.fridge.domain.Product;
import memphis.fridge.exceptions.InvalidAmountException;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 30/09/12
 */
public class CurrencyUtils {

	private static final BigDecimal CENTS_SCALE = new BigDecimal(BigInteger.valueOf(100), 0);

	public static BigDecimal fromCents(int cents) {
		return BigDecimal.valueOf(cents, 2);
	}

	public static BigDecimal fromPercent(int percent) {
		return BigDecimal.valueOf(percent * 10, 1);
	}

	public static BigDecimal markup(BigDecimal amount, BigDecimal markup) {
		BigDecimal scale = markup.movePointLeft(2);
		return amount.multiply(scale).setScale(2, RoundingMode.HALF_UP);
	}

	public static BigDecimal calculateCost(Product product, int number) {
		return product.getCost().multiply(BigDecimal.valueOf(number));
	}

	public static BigDecimal calculateMarkup(Product product, int number, BigDecimal tax) {
		BigDecimal markup = product.getMarkup();
		if (tax != null) {
			markup = markup.add(tax);
		}
		return markup(calculateCost(product, number), markup);
	}

	public static int toCents(BigDecimal amount) {
		try {
			return amount.multiply(CENTS_SCALE).intValueExact();
		} catch (ArithmeticException ex) {
			throw new InvalidAmountException(ex);
		}
	}

	public static int toPercent(BigDecimal amount) {
		try {
			return amount.intValueExact();
		} catch (ArithmeticException ex) {
			throw new InvalidAmountException(ex);
		}
	}
}
