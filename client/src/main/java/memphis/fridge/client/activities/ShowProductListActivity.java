package memphis.fridge.client.activities;

import java.util.List;
import java.util.logging.Logger;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import javax.inject.Inject;
import javax.inject.Provider;
import memphis.fridge.client.events.ProductEvent;
import memphis.fridge.client.rpc.Messages;
import memphis.fridge.client.rpc.ProductRequest;
import memphis.fridge.client.views.ProductView;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 7/10/12
 */
public class ShowProductListActivity extends AbstractActivity implements ProductView.Presenter {

	private static final Logger log = Logger.getLogger("product list");

	@Inject
	ProductView view;

	@Inject
	Provider<ProductRequest> req;

	@Inject
	EventBus eventBus;

	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		view.setPresenter(this);
		panel.setWidget(view);

		req.get().requestProducts(null, new ProductRequest.Handler() {
			public void productsReady(List<? extends Messages.Stock> products) {
				view.setProducts(products);
			}
		});
	}

	public void productSelected(Messages.Stock product) {
		log.info(product.getProductCode() + " selected");
		eventBus.fireEvent(new ProductEvent(product));
	}
}
