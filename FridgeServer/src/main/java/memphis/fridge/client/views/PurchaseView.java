package memphis.fridge.client.views;

import com.google.gwt.user.client.ui.IsWidget;

import memphis.fridge.client.rpc.Product;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 7/10/12
 */
public interface PurchaseView extends IsWidget {

	void setPresenter(Presenter presenter);

	void addProduct(Product product, int count);

	interface Presenter {
		void addToOrder(String code, String num);
	}
}
