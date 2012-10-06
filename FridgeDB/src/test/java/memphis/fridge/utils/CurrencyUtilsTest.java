package memphis.fridge.utils;

import java.math.BigDecimal;

import memphis.fridge.domain.Product;
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

	@Test
	public void testMarkup() {
		BigDecimal v0_00 = BigDecimal.valueOf(0, 2);
		BigDecimal v0_20 = BigDecimal.valueOf(20, 2);
		BigDecimal v1_00 = BigDecimal.valueOf(100, 2);
		BigDecimal v1_20 = BigDecimal.valueOf(120, 2);
		BigDecimal v20_0 = BigDecimal.valueOf(200, 1);
		assertEquals(v0_00, CurrencyUtils.markup(BigDecimal.ZERO, BigDecimal.ZERO));
		assertEquals(v0_00, CurrencyUtils.markup(BigDecimal.ZERO, BigDecimal.ONE));
		assertEquals(v0_00, CurrencyUtils.markup(BigDecimal.ONE, BigDecimal.ZERO));
		assertEquals(v0_20, CurrencyUtils.markup(v1_00, v20_0));
	}

	@Test
	public void testCalculateCost() {
		Product product = new Product("CC", "Coke", BigDecimal.valueOf(100, 2), BigDecimal.valueOf(200, 1), null);
		BigDecimal cost = CurrencyUtils.calculateCost(product, 2);
		assertEquals(BigDecimal.valueOf(200, 2), cost);
	}

	@Test
	public void testCalculateMarkupGrad() {
		Product product = new Product("CC", "Coke", BigDecimal.valueOf(100, 2), BigDecimal.valueOf(200, 1), null);
		BigDecimal markup = CurrencyUtils.calculateMarkup(product, 2, BigDecimal.ZERO);
		assertEquals(BigDecimal.valueOf(2 * 20, 2), markup);
	}

	@Test
	public void testCalculateMarkupUndergrad() {
		Product product = new Product("CC", "Coke", BigDecimal.valueOf(100, 2), BigDecimal.valueOf(200, 1), null);
		BigDecimal markup = CurrencyUtils.calculateMarkup(product, 2, BigDecimal.valueOf(100, 1));
		assertEquals(BigDecimal.valueOf(2 * (10 + 20), 2), markup);
	}
}
