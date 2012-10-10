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
import memphis.fridge.client.events.AccountEvent;
import memphis.fridge.client.events.AccountHandler;
import memphis.fridge.client.events.ProductEvent;
import memphis.fridge.client.events.ProductHandler;
import memphis.fridge.client.places.SessionPlace;
import memphis.fridge.client.rpc.*;
import memphis.fridge.client.views.PurchaseView;

import static memphis.fridge.client.utils.NumberUtils.printCurrency;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 7/10/12
 */
public class PurchaseActivity extends AbstractActivity implements PurchaseView.Presenter {

	private static final Logger log = Logger.getLogger("purchase");

	@Inject
	PurchaseView view;

	@Inject
	Session session;

	@Inject
	Provider<RequestProducts> productsRequest;

	@Inject
	Provider<RequestNonce> nonce;

	@Inject
	Provider<RequestPurchase> purchase;

	private SessionPlace details;

	private Map<String, Product> products;
	private Map<String, Integer> order = Maps.newHashMap();

	public PurchaseActivity() {
	}

	public PurchaseActivity init(SessionPlace details) {
		this.details = details;
		return this;
	}

	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		view.setPresenter(this);
		refreshProducts();
		refreshView();
		panel.setWidget(view);

		ProductEvent.register(eventBus, new ProductHandler() {
			public void productSelected(Product p) {
				addToOrder(p.getProductCode(), 1);
				view.setProduct(p.getProductCode());
			}
		});
		AccountEvent.register(eventBus, new AccountHandler() {
			public void accountAvailable(Account a) {
				view.setBalance(a.getBalance());
			}
		});
	}

	@Override
	public String mayStop() {
		if (!order.isEmpty()) {
			return "Do you want to abandon your order?";
		}
		return null;
	}

	@Override
	public void onStop() {
		view.setPresenter(null);
	}

	public void addToOrder(String code, int num) {
		code = code.toUpperCase();
		if (products == null || products.containsKey(code)) {
			if (order.containsKey(code)) {
				int count = order.get(code) + num;
				if (count != 0) {
					order.put(code, order.get(code) + num);
				} else {
					order.remove(code);
				}
			} else {
				order.put(code, num);
			}
		}
		refreshView();
	}

	public void submitOrder() {
		final List<PurchaseEntry> content = buildOrder();
		int total = 0;
		for (PurchaseEntry e : content) {
			total += e.getCost();
		}
		boolean confirm = Window.confirm("Your account will be charged " + printCurrency(total).asString());
		if (confirm) {
			final RequestPurchase.OrderResponseHandler callback = new RequestPurchase.OrderResponseHandler() {
				public void onOrderProcessed(int balance, int orderTotal) {
					order.clear();
					Window.alert("Success! Your balance is now " + printCurrency(balance).asString());
					session.logout();
				}

				public void onError(Throwable exception) {
					log.warning("error placing order: " + exception.getMessage());
					// TODO better error handling
					Window.alert("failed to place order!");
				}
			};

			nonce.get().requestNonce(details, new RequestNonce.Handler() {
				public void onNonceReceived(String snonce) {
					purchase.get().requestOrder(snonce, details.getUsername(), content, callback);
				}

				public void onError(Throwable exception) {
					callback.onError(exception);
				}
			});
		}
	}

	private void refreshView() {
		List<PurchaseEntry> content = buildOrder();
		view.setCartContents(content);
		if (content != null) {
			int cost = 0;
			for (PurchaseEntry e : content) {
				cost += e.getCost();
			}
			view.setOrderDetails(cost);
		}
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
		productsRequest.get().requestProducts(details.getUsername(), new RequestProducts.Handler() {
			public void productsReady(List<? extends Product> incoming) {
				products = Maps.newHashMap();
				for (Product p : incoming) {
					products.put(p.getProductCode().toUpperCase(), p);
				}
				List<String> toRemove = Lists.newArrayList();
				for (String code : order.keySet()) {
					if (!products.containsKey(code)) toRemove.add(code);
				}
				for (String code : toRemove) {
					order.remove(code);
				}
				refreshView();
			}
		});
	}
}
