package memphis.fridge.client.rpc;

import java.util.List;
import java.util.logging.Logger;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.http.client.*;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.safehtml.shared.UriUtils;

import com.google.common.collect.Lists;
import javax.inject.Inject;
import memphis.fridge.client.utils.CryptUtils;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 8/10/12
 */
public class RequestPurchase {
	private static final Logger log = Logger.getLogger("RequestNonce");

	public static interface OrderResponseHandler {
		public void onOrderProcessed(int balance, int orderTotal);

		public void onError(Throwable exception);
	}

	@Inject
	CryptUtils crypt;

	@Inject
	Session session;

	public void requestOrder(String snonce, String username, List<PurchaseEntry> cart, OrderResponseHandler request) {
		List<Object> hmacParams = Lists.<Object>newArrayList(snonce, username);
		for (PurchaseEntry p : cart) {
			hmacParams.add(p.getProductCode());
			hmacParams.add(p.getNumber());
		}
		String hmac = session.sign(hmacParams.toArray());

		SafeUri uri = createRequest();
		OrderRequest params = OrderRequest.createRequest();
		params.setNonce(snonce);
		params.setUsername(username);
		for (PurchaseEntry p : cart) {
			params.addItem(p.getProductCode(), p.getNumber());
		}
		params.setHmac(hmac);

		RequestBuilder builder = new RequestBuilder(RequestBuilder.POST, uri.asString());
		builder.setHeader("Content-Type", "application/json");
		builder.setRequestData(params.serialize());
		builder.setCallback(new Callback(session, snonce, request));
		try {
			builder.send();
		} catch (RequestException ex) {
			log.warning("error sending request: " + ex.getMessage());
		}
	}

	static SafeUri createRequest() {
		return UriUtils.fromSafeConstant(
				"/fridge/rest/purchase/json"
		);
	}

	private static class Callback implements RequestCallback {
		private final Session session;
		private final String nonce;
		private final OrderResponseHandler handler;

		public Callback(Session session, String nonce, OrderResponseHandler handler) {
			this.session = session;
			this.nonce = nonce;
			this.handler = handler;
		}

		public void onResponseReceived(Request request, Response response) {
			if (response.getStatusCode() == 200) {
				log.info("response: " + response.getStatusCode() + " " + response.getStatusText());
				try {
					OrderResponse r = JsonUtils.safeEval(response.getText());
					if (session.verify(r.getHMAC(), nonce, r.getBalance(), r.getOrderTotal())) {
						handler.onOrderProcessed(r.getBalance(), r.getOrderTotal());
					} else {
						handler.onError(new RuntimeException("nonce failed verification"));
					}
				} catch (IllegalArgumentException ex) {
					log.warning("parsing nonce response failed.\n" + ex.getMessage());
					handler.onError(ex);
				}
			} else {
				log.info("unexpected response status: " + response.getStatusCode() + " " + response.getStatusText());
			}
		}

		public void onError(Request request, Throwable exception) {
			handler.onError(exception);
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
}
