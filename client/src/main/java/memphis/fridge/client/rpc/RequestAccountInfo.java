package memphis.fridge.client.rpc;

import java.util.logging.Logger;

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
 * Date: 10/10/12
 */
public class RequestAccountInfo extends FridgeRequest {

	private static final Logger log = Logger.getLogger("account-info");

	public static interface Handler {
		public void onAccountReady(Account account);

		public void onError(Throwable exception);
	}

	@Inject
	CryptUtils crypt;

	public void requestAccountInfo(SessionPlace details, String nonce, Handler callback) {
		final String message = "{\"username\":\"" + details.getUsername() + "\"}";

		SafeUri uri = url();
		RequestBuilder builder = initRequest(url(), RequestBuilder.POST, details, nonce, message);
		builder.setHeader("Content-Type", "application/json");
		builder.setRequestData(message);
		builder.setCallback(new Callback(details, nonce, callback));
		try {
			builder.send();
		} catch (RequestException ex) {
			log.warning("error sending request: " + ex.getMessage());
		}
	}

	private class Callback extends FridgeRequest.Callback<Handler> {
		private Callback(SessionPlace details, String nonce, Handler handler) {
			super(details, nonce, handler, 200);
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
