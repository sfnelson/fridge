package memphis.fridge.client.rpc;

import com.google.gwt.safehtml.shared.SafeUri;

/**
 * @author stephen
 */
public interface Product {

    String getProductCode();

    String getDescription();

    int getInStock();

    String getInStockText();

    int getPrice();

    String getPriceText();

    String getCategory();

    int getCategoryOrder();

    SafeUri getImageHref();

}
