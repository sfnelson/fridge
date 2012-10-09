package memphis.fridge.client.views;

import java.util.List;

import com.google.gwt.user.client.ui.IsWidget;

import memphis.fridge.client.rpc.Product;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 7/10/12
 */
public interface ProductView extends IsWidget {

	void setProducts(List<? extends Product> products);

}
