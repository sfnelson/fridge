package memphis.fridge.client.rpc;

import java.util.List;
import java.util.logging.Logger;

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
public class PurchaseRequest extends AbstractSignedRequest {
	private static final Logger log = Logger.getLogger("RequestNonce");

	private static final String VERB = "purchase-request";

	public static interface Handler {
		public void onOrderProcessed(int balance, int orderTotal);

		public void onError(String reason);
	}

	public void requestOrder(SessionPlace details, List<PurchaseEntry> cart, Handler callback) {
		init(details);

		Messages.PurchaseRequest params = Messages.createPurchaseRequest();
		for (PurchaseEntry p : cart) {
			params.addProduct(p.getProductCode(), p.getNumber());
		}
		String message = params.toJSON().toString();

		try {
			request(RequestBuilder.POST, url(), VERB, message, new Callback(this, callback)).send();
		} catch (RequestException ex) {
			log.warning("error sending request: " + ex.getMessage());
		}
	}

	@Override
	Logger getLog() {
		return log;
	}

	private static class Callback extends AbstractSignedRequest.Callback<Handler> {
		private Callback(PurchaseRequest req, Handler handler) {
			super(req, 200, handler);
		}

		@Override
		protected void doCallbackSuccess(Handler handler, String message, Response response) {
			Messages.TransactionResponse r = Messages.parseTransactionResponse(message);
			handler.onOrderProcessed(r.getBalance(), r.getCost());
		}

		@Override
		protected void doCallbackFailure(Handler handler, String message) {
			handler.onError(message);
		}
	}

	static SafeUri url() {
		return UriUtils.fromSafeConstant(
				"/memphis/fridge/rest/purchase/json"
		);
	}
}
