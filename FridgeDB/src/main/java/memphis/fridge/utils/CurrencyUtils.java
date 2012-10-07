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

	private static final BigDecimal scale = new BigDecimal(BigInteger.valueOf(100), 0);

	public static BigDecimal fromCents(int cents) {
		return BigDecimal.valueOf(cents, 2);
	}

	public static BigDecimal fromPercent(int percent) {
		return BigDecimal.valueOf(percent * 10, 1);
	}

	public static BigDecimal markup(BigDecimal amount, BigDecimal markup) {
		BigDecimal scale = markup.divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP);
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
			return amount.multiply(scale).intValueExact();
		} catch (ArithmeticException ex) {
			throw new InvalidAmountException(ex);
		}
	}
}
