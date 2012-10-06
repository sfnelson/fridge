package memphis.fridge.server;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import com.google.common.collect.Lists;
import memphis.fridge.domain.Product;
import memphis.fridge.domain.ProductCategory;
import memphis.fridge.utils.Pair;

import static memphis.fridge.utils.CurrencyUtils.*;
import static memphis.fridge.utils.Pair.pair;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 6/10/12
 */
public class TestingData {

	public static final String USERNAME = "{USERNAME}";
	public static final String SNONCE = "{SNONCE}";
	public static final String HMAC = "{HMAC}";

	public static final ProductCategory DRINKS = new ProductCategory("Drinks", 1);

	public static final BigDecimal COKE_BASE = fromCents(100);
	public static final BigDecimal COKE_TAX_RATE = fromPercent(20);
	public static final BigDecimal COKE_TAX_UNIT = markup(COKE_BASE, COKE_TAX_RATE);
	public static final int COKE_STOCK = 18;

	public static Product coke() {
		Product coke = new Product("CC", "Coke Can", COKE_BASE, COKE_TAX_RATE, DRINKS);
		coke.setInStock(COKE_STOCK);
		return coke;
	}

	public static BigDecimal cokeTax(boolean isGrad) {
		return markup(COKE_BASE, isGrad ? COKE_TAX_RATE : COKE_TAX_RATE.add(GRAD_TAX));
	}

	public static BigDecimal cokeTotal(boolean isGrad) {
		return COKE_BASE.add(cokeTax(isGrad));
	}

	public static final ProductCategory FOOD = new ProductCategory("Food", 2);

	public static final BigDecimal COOKIE_BASE = fromCents(200);
	public static final BigDecimal COOKIE_TAX_RATE = fromPercent(20);
	public static final BigDecimal COOKIE_TAX_UNIT = markup(COOKIE_BASE, COOKIE_TAX_RATE);
	public static final int COOKIE_STOCK = 9;

	public static Product cookie() {
		Product cookie = new Product("CT", "Cookie Time", COOKIE_BASE, COOKIE_TAX_RATE, FOOD);
		cookie.setInStock(COOKIE_STOCK);
		return cookie;
	}

	public static BigDecimal cookieTax(boolean isGrad) {
		return markup(COOKIE_BASE, isGrad ? COOKIE_TAX_RATE : COOKIE_TAX_RATE.add(GRAD_TAX));
	}

	public static BigDecimal cookieTotal(boolean isGrad) {
		return COOKIE_BASE.add(cookieTax(isGrad));
	}

	public static List<Product> products() {
		return Lists.newArrayList(coke(), cookie());
	}

	public static final int ORDER_NUM_COKE = 2;
	public static final int ORDER_NUM_COOKIE = 1;

	@SuppressWarnings("unchecked")
	private static final Pair<String, Integer>[] order = new Pair[]{
			pair("CC", ORDER_NUM_COKE),
			pair("CT", ORDER_NUM_COOKIE)
	};

	public static List<Pair<String, Integer>> order() {
		return Arrays.asList(order);
	}

	public static final BigDecimal GRAD_TAX = fromPercent(10);

	public static BigDecimal orderCokeBase() {
		return BigDecimal.valueOf(ORDER_NUM_COKE).multiply(COKE_BASE);
	}

	public static BigDecimal orderCokeTax(boolean isGrad) {
		return markup(orderCokeBase(), isGrad ? COKE_TAX_RATE : COKE_TAX_RATE.add(GRAD_TAX));
	}

	public static BigDecimal orderCokeTotal(boolean isGrad) {
		return orderCokeBase().add(orderCokeTax(isGrad));
	}

	public static BigDecimal orderCookieBase() {
		return BigDecimal.valueOf(ORDER_NUM_COOKIE).multiply(COOKIE_BASE);
	}

	public static BigDecimal orderCookieTax(boolean isGrad) {
		return markup(orderCookieBase(), isGrad ? COOKIE_TAX_RATE : COOKIE_TAX_RATE.add(GRAD_TAX));
	}

	public static BigDecimal orderCookieTotal(boolean isGrad) {
		return orderCookieBase().add(orderCookieTax(isGrad));
	}

	public static BigDecimal orderBaseTotal(boolean isGrad) {
		return orderCokeBase().add(orderCookieBase());
	}

	public static BigDecimal orderTaxTotal(boolean isGrad) {
		return orderCokeTax(isGrad).add(orderCookieTax(isGrad));
	}

	public static BigDecimal orderTotal(boolean isGrad) {
		return orderCokeTotal(isGrad).add(orderCookieTotal(isGrad));
	}
}
