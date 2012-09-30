package memphis.fridge.utils;

import java.math.BigDecimal;

import memphis.fridge.exceptions.InvalidAmountException;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 30/09/12
 */
public class CurrencyUtilsTest {
	@Test
	public void testFromCents() throws Exception {
		BigDecimal val = new BigDecimal("10.00");
		assertEquals(val, CurrencyUtils.fromCents(1000));
	}

	@Test
	public void testNegativeFromCents() throws Exception {
		BigDecimal val = new BigDecimal("-10.00");
		assertEquals(val, CurrencyUtils.fromCents(-1000));
	}

	@Test(expected = InvalidAmountException.class)
	public void testToCentsBad() throws Exception {
		BigDecimal val = new BigDecimal("10.001");
		CurrencyUtils.toCents(val);
	}

	@Test
	public void testFromPercent() throws Exception {
		BigDecimal val = new BigDecimal("10.0");
		assertEquals(val, CurrencyUtils.fromPercent(10));
	}

	@Test
	public void testToCents() throws Exception {
		BigDecimal val = new BigDecimal("10.00");
		assertEquals(1000, CurrencyUtils.toCents(val));
	}

	@Test
	public void testNegativeToCents() throws Exception {
		BigDecimal val = new BigDecimal("-10.00");
		assertEquals(-1000, CurrencyUtils.toCents(val));
	}
}
