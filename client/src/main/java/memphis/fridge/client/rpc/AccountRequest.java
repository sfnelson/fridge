package memphis.fridge.client.rpc;

import java.util.logging.Logger;

import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.safehtml.shared.UriUtils;

import memphis.fridge.client.places.SessionPlace;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 10/10/12
 */
public class AccountRequest extends AbstractSignedRequest {

	private static final String VERB = "account-request";

	private static final Logger log = Logger.getLogger("account-info");

	public static interface Handler {
		public void onAccountReady(Account account);

		public void onError(Throwable exception);
	}

	public void requestAccountInfo(SessionPlace details, Handler callback) {
		init(details);

		final String message = "{\"username\":\"" + details.getUsername() + "\"}";

		try {
			request(RequestBuilder.POST, url(), VERB, message, new Callback(this, callback)).send();
		} catch (RequestException ex) {
			log.warning("error sending request: " + ex.getMessage());
		}
	}

	private static class Callback extends AbstractSignedRequest.Callback<Handler> {
		private Callback(AccountRequest req, Handler handler) {
			super(req, 200, handler);
		}

		@Override
		protected void doCallbackSuccess(Handler handler, String message, Response response) {
			Account a = JsonUtils.safeEval(message);
			handler.onAccountReady(a);
		}

		@Override
		protected void doCallbackFailure(Handler handler, Throwable ex) {
			log.warning("error requesting account info: " + ex.getMessage());
			handler.onError(ex);
		}
	}

	static SafeUri url() {
		return UriUtils.fromSafeConstant(
				"/memphis/fridge/rest/account/info/json"
		);
	}
}
