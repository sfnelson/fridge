package memphis.fridge.client.rpc;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.safehtml.shared.UriUtils;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 7/10/12
 */
final class ProductJS extends JavaScriptObject implements Product {

    static native JsArray<ProductJS> parse(String json) /*-{
        return eval('(' + json + ')').stock;
    }-*/;

    protected ProductJS() {
    }

    public native String getProductCode() /*-{
        return this.product_code;
    }-*/;

    public native String getDescription() /*-{
        return this.description;
    }-*/;

    public native int getInStock() /*-{
        return this.in_stock;
    }-*/;

    public native String getInStockText() /*-{
        return "" + this.in_stock;
    }-*/;

    public native int getPrice() /*-{
        return this.price;
    }-*/;

    public String getPriceText() {
        int price = getPrice();
        int mod = price % 100;
        return "$" + price / 100 + "." + (mod < 10 ? "0" : "") + mod;
    }

    public native String getCategory() /*-{
        return this.category;
    }-*/;

    public native int getCategoryOrder() /*-{
        return this.category_order;
    }-*/;

    public SafeUri getImageHref() {
        return UriUtils.fromTrustedString("/memphis/fridge/rest/images?product="
                + UriUtils.encode(getProductCode()));
    }
}
