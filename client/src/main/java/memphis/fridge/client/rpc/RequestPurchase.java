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

import javax.inject.Inject;
import memphis.fridge.client.places.SessionPlace;
import memphis.fridge.client.utils.CryptUtils;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 8/10/12
 */
public class RequestPurchase extends FridgeRequest {
	private static final Logger log = Logger.getLogger("RequestNonce");

	public static interface OrderResponseHandler {
		public void onOrderProcessed(int balance, int orderTotal);

		public void onError(Throwable exception);
	}

	@Inject
	CryptUtils crypt;

	@Inject
	Session session;

	public void requestOrder(String nonce, SessionPlace details, List<PurchaseEntry> cart, OrderResponseHandler request) {
		OrderRequest params = OrderRequest.createRequest();
		for (PurchaseEntry p : cart) {
			params.addItem(p.getProductCode(), p.getNumber());
		}
		String message = params.serialize();

		RequestBuilder builder = initRequest(url(), RequestBuilder.POST, details, nonce, message);
		builder.setHeader("Content-Type", "application/json");
		builder.setRequestData(message);
		builder.setCallback(new Callback(details, nonce, request));
		try {
			builder.send();
		} catch (RequestException ex) {
			log.warning("error sending request: " + ex.getMessage());
		}
	}

	private class Callback extends FridgeRequest.Callback<OrderResponseHandler> {
		private Callback(SessionPlace details, String nonce, OrderResponseHandler handler) {
			super(details, nonce, handler, 200);
		}

		@Override
		protected void doCallbackSuccess(OrderResponseHandler handler, String message, Response response) {
			OrderResponse r = JsonUtils.safeEval(message);
			handler.onOrderProcessed(r.getBalance(), r.getOrderTotal());
		}

		@Override
		protected void doCallbackFailure(OrderResponseHandler handler, Throwable ex) {
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
