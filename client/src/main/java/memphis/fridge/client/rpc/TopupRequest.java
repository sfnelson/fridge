package memphis.fridge.client.rpc;

import java.util.logging.Logger;

import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.safehtml.shared.UriUtils;

import memphis.fridge.client.places.SessionPlace;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 22/10/12
 */
public class TopupRequest extends AbstractSignedRequest {
	private static final String VERB = "topup-request";

	private static final Logger log = Logger.getLogger("topup-request");

	public static interface Handler {
		public void onSuccess(int balance, int cost);

		public void onError(String reason);
	}

	public void requestTopup(SessionPlace details, int cents, Handler callback) {
		init(details);

		JSONObject message = Messages.createTopupRequest(cents);

		try {
			request(RequestBuilder.POST, url(), VERB, message.toString(), new Callback(this, callback)).send();
		} catch (RequestException ex) {
			log.warning("error sending request: " + ex.getMessage());
		}
	}

	@Override
	Logger getLog() {
		return log;
	}

	private static class Callback extends AbstractSignedRequest.Callback<Handler> {
		private Callback(TopupRequest req, Handler handler) {
			super(req, 200, handler);
		}

		@Override
		protected void doCallbackSuccess(Handler handler, String message, Response response) {
			Messages.TransactionResponse resp = Messages.parseTransactionResponse(message);
			handler.onSuccess(resp.getBalance(), resp.getCost());
		}

		@Override
		protected void doCallbackFailure(Handler handler, String reason) {
			handler.onError(reason);
		}
	}

	static SafeUri url() {
		return UriUtils.fromSafeConstant(
				"/memphis/fridge/rest/account/topup/json"
		);
	}
}
