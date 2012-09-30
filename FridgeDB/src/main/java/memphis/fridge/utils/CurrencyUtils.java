package memphis.fridge.utils;

import java.math.BigDecimal;
import java.math.BigInteger;

import memphis.fridge.exceptions.InvalidAmountException;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 30/09/12
 */
public class CurrencyUtils {

	private static final BigDecimal scale = new BigDecimal(BigInteger.valueOf(100), 0);

	public static BigDecimal fromCents(int cents) {
		return new BigDecimal(BigInteger.valueOf(cents), 2);
	}

	public static BigDecimal fromPercent(int percent) {
		return new BigDecimal(BigInteger.valueOf(percent * 10), 1);
	}

	public static int toCents(BigDecimal amount) {
		try {
			return amount.multiply(scale).intValueExact();
		} catch (ArithmeticException ex) {
			throw new InvalidAmountException(ex);
		}
	}
}
