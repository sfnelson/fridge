package memphis.fridge.client.activities;

import java.util.Map;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.google.common.collect.Maps;
import javax.inject.Inject;
import memphis.fridge.client.rpc.Product;
import memphis.fridge.client.views.PurchaseView;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 7/10/12
 */
public class PurchaseActivity extends AbstractActivity implements PurchaseView.Presenter {

	@Inject
	PurchaseView view;

	private Map<Product, Integer> order = Maps.newHashMap();

	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		view.setPresenter(this);
		panel.setWidget(view);
	}

	public void addToOrder(String code, String num) {
	}
}
