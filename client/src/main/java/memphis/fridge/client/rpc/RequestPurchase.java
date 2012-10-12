package memphis.fridge.client.rpc;

import java.util.List;
import java.util.logging.Logger;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.safehtml.shared.UriUtils;

import memphis.fridge.client.places.SessionPlace;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 8/10/12
 */
public class RequestPurchase extends FridgeRequest {
	private static final Logger log = Logger.getLogger("RequestNonce");

	private static final String VERB = "purchase-request";

	public static interface Handler {
		public void onOrderProcessed(int balance, int orderTotal);

		public void onError(Throwable exception);
	}

	public void requestOrder(SessionPlace details, List<PurchaseEntry> cart, Handler callback) {
		init(details);

		OrderRequest params = OrderRequest.createRequest();
		for (PurchaseEntry p : cart) {
			params.addItem(p.getProductCode(), p.getNumber());
		}
		String message = params.serialize();

		try {
			request(RequestBuilder.POST, url(), VERB, message, new Callback(this, callback)).send();
		} catch (RequestException ex) {
			log.warning("error sending request: " + ex.getMessage());
		}
	}

	private static class Callback extends FridgeRequest.Callback<Handler> {
		private Callback(RequestPurchase req, Handler handler) {
			super(req, 200, handler);
		}

		@Override
		protected void doCallbackSuccess(Handler handler, String message, Response response) {
			OrderResponse r = JsonUtils.safeEval(message);
			handler.onOrderProcessed(r.getBalance(), r.getOrderTotal());
		}

		@Override
		protected void doCallbackFailure(Handler handler, Throwable ex) {
			handler.onError(ex);
		}
	}

	private static class OrderRequest extends JavaScriptObject {

		public static native OrderRequest createRequest() /*-{
			return ({});
		}-*/;

		protected OrderRequest() {
		}

		public native final void setNonce(String nonce) /*-{
			this.nonce = nonce;
		}-*/;

		public native final void setUsername(String username) /*-{
			this.username = username;
		}-*/;

		public native final void addItem(String code, int quantity) /*-{
			this.items = this.items || [];
			this.items.push({'code':code, 'quantity':quantity });
		}-*/;

		public native final void setHmac(String hmac) /*-{
			this.hmac = hmac;
		}-*/;

		public native final String serialize() /*-{
			return JSON.stringify(this);
		}-*/;
	}

	private static class OrderResponse extends JavaScriptObject {

		protected OrderResponse() {
		}

		public native final int getBalance() /*-{
			return this.balance;
		}-*/;

		public native final int getOrderTotal() /*-{
			return this.order_total;
		}-*/;

		public native final String getHMAC() /*-{
			return this.hmac;
		}-*/;
	}

	static SafeUri url() {
		return UriUtils.fromSafeConstant(
				"/memphis/fridge/rest/purchase/json"
		);
	}
}
