package memphis.fridge.client.activities;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import javax.inject.Inject;
import javax.inject.Provider;
import memphis.fridge.client.rpc.*;
import memphis.fridge.client.utils.NumberUtils;
import memphis.fridge.client.views.PurchaseView;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 7/10/12
 */
public class PurchaseActivity extends AbstractActivity implements PurchaseView.Presenter, RequestProducts.ProductRequestHandler {

	private static final Logger log = Logger.getLogger("purchase");

	@Inject
	PurchaseView view;

	@Inject
	Session session;

	@Inject
	Provider<RequestProducts> productsRequest;

	private String username;

	private Map<String, Product> products;
	private Map<String, Integer> order = Maps.newHashMap();

	public PurchaseActivity() {
	}

	public PurchaseActivity init(String username) {
		this.username = username;
		return this;
	}

	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		if (!session.isLoggedIn()) session.logout();
		view.setPresenter(this);
		refreshProducts();
		refreshView();
		panel.setWidget(view);
	}

	public void addToOrder(String code, int num) {
		if (products == null || products.containsKey(code)) {
			if (order.containsKey(code)) {
				order.put(code, order.get(code) + num);
			} else {
				order.put(code, Integer.valueOf(num));
			}
		}
		refreshView();
	}

	public void submitOrder() {
		List<PurchaseEntry> content = buildOrder();
		int total = 0;
		for (PurchaseEntry e : content) {
			total += e.getCost();
		}
		boolean confirm = Window.confirm("Your account will be charged " + NumberUtils.printCurrency(total).asString());
		if (confirm) {
			session.placeOrder(content, new RequestPurchase.OrderResponseHandler() {
				public void onOrderProcessed(int balance, int orderTotal) {
					Window.alert("Success! Your balance is now " + NumberUtils.printCurrency(balance).asString());

					session.logout();
				}

				public void onError(Throwable exception) {
					log.warning("error placing order: " + exception.getMessage());
					// TODO better error handling
					Window.alert("failed to place order!");
				}
			});
		}
	}

	private void refreshView() {
		List<PurchaseEntry> content = buildOrder();
		view.setCartContents(content);
	}

	private List<PurchaseEntry> buildOrder() {
		if (products == null) return null;
		List<PurchaseEntry> content = Lists.newArrayList();
		for (String code : order.keySet()) {
			Product p = products.get(code);
			if (p != null) {
				content.add(new PurchaseEntry(p, order.get(code)));
			}
		}
		return content;
	}

	private void refreshProducts() {
		products = null;
		productsRequest.get().requestProducts(username, this);
	}

	public void productsReady(List<? extends Product> products) {
		this.products = Maps.newHashMap();
		for (Product p : products) {
			this.products.put(p.getProductCode(), p);
		}
		List<String> toRemove = Lists.newArrayList();
		for (String code : order.keySet()) {
			if (!this.products.containsKey(code)) toRemove.add(code);
		}
		for (String code : toRemove) {
			order.remove(code);
		}
		refreshView();
	}
}
