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

	public void setCartContents(List<PurchaseEntry> content);

	interface Presenter {
		void addToOrder(String code, int num);

		void submitOrder();
	}
}
