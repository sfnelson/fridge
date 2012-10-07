package memphis.fridge.client.rpc;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 7/10/12
 */
public class Product extends JavaScriptObject {

	public static native JsArray<Product> parse(String json) /*-{
		return eval('(' + json + ')');
	}-*/;

	protected Product() {
	}

	public final native String getProductCode() /*-{
		return this.product_code;
	}-*/;

	public final native String getDescription() /*-{
		return this.description;
	}-*/;

	public final native int getInStock() /*-{
		return this.in_stock;
	}-*/;

	public final native String getInStockText() /*-{
		return "" + this.in_stock;
	}-*/;

	public final native int getPrice() /*-{
		return this.price;
	}-*/;

	public final String getPriceText() {
		int price = getPrice();
		int mod = price % 100;
		return "$" + price / 100 + "." + (mod < 10 ? "0" : "") + mod;
	}

	public final native String getCategory() /*-{
		return this.category;
	}-*/;

	public final native int getCategoryOrder() /*-{
		return this.category_order;
	}-*/;
}
