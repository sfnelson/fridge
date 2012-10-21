package memphis.fridge.client.views;

import java.util.List;

import com.google.gwt.user.client.ui.IsWidget;

import memphis.fridge.client.rpc.Messages;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 7/10/12
 */
public interface ProductView extends IsWidget {

	void setPresenter(Presenter presenter);

	void setProducts(List<? extends Messages.Stock> products);

	interface Presenter {
		void productSelected(Messages.Stock product);
	}

}
