package memphis.fridge.client.rpc;

import java.util.logging.Logger;

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
		public void onAccountReady(Messages.Account account);

		public void onError(String message);
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

	@Override
	Logger getLog() {
		return log;
	}

	private static class Callback extends AbstractSignedRequest.Callback<Handler> {
		private Callback(AccountRequest req, Handler handler) {
			super(req, 200, handler);
		}

		@Override
		protected void doCallbackSuccess(Handler handler, String message, Response response) {
			Messages.Account a = Messages.parseAccountResponse(message);
			handler.onAccountReady(a);
		}

		@Override
		protected void doCallbackFailure(Handler handler, String message) {
			handler.onError(message);
		}
	}

	static SafeUri url() {
		return UriUtils.fromSafeConstant(
				"/memphis/fridge/rest/account/info/json"
		);
	}
}
