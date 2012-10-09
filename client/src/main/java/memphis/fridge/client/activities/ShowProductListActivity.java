package memphis.fridge.client.activities;

import java.util.List;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import javax.inject.Inject;
import javax.inject.Provider;

import memphis.fridge.client.rpc.Product;
import memphis.fridge.client.rpc.RequestProducts;
import memphis.fridge.client.views.ProductView;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 7/10/12
 */
public class ShowProductListActivity extends AbstractActivity implements RequestProducts.ProductRequestHandler {

	@Inject
    ProductView view;

	@Inject
	Provider<RequestProducts> req;

	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		panel.setWidget(view);

		req.get().requestProducts(null, this);
	}

	public void productsReady(List<? extends Product> products) {
		view.setProducts(products);
	}
}
