package memphis.fridge.client.rpc;

import javax.validation.constraints.NotNull;
import memphis.fridge.client.utils.NumberUtils;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 8/10/12
 */
public class PurchaseEntry {

	@NotNull
	private final Messages.Stock product;

	private int number;

	public PurchaseEntry(@NotNull Messages.Stock product, int number) {
		this.product = product;
		this.number = number;
	}

	@NotNull
	public Messages.Stock getProduct() {
		return product;
	}

	@NotNull
	public String getProductCode() {
		return product.getProductCode();
	}

	@NotNull
	public String getDescription() {
		return product.getDescription();
	}

	public int getNumber() {
		return number;
	}

	@NotNull
	public String getNumberText() {
		return String.valueOf(number);
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public int getCost() {
		return product.getPrice() * number;
	}

	public String getCostText() {
		return NumberUtils.printCurrency(getCost()).asString();
	}
}
