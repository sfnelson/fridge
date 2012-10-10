package memphis.fridge.client.views;

import java.util.List;

import com.google.gwt.user.client.ui.IsWidget;

import memphis.fridge.client.rpc.PurchaseEntry;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 7/10/12
 */
public interface PurchaseView extends IsWidget {

	void setPresenter(Presenter presenter);

	void setProduct(String productCode);

	void setCartContents(List<PurchaseEntry> content);

	void setOrderDetails(int cost);

	void setBalance(int balance);

	void clearOrder();

	interface Presenter {
		void addToOrder(String code, int num);

		void submitOrder();
	}
}
