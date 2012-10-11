package memphis.fridge.client.rpc;

import java.util.logging.Logger;

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
 * Date: 30/09/12
 */
public class RequestNonce extends FridgeRequest {

	private static final Logger log = Logger.getLogger("RequestNonce");

	public static interface Handler {
		public void onNonceReceived(String snonce);

		public void onError(Throwable exception);
	}

	@Inject
	CryptUtils crypt;

	public void requestNonce(SessionPlace details, Handler callback) {
		final String nonce = crypt.generateNonceToken();
		final int timestamp = (int) (System.currentTimeMillis() / 1000);

		RequestBuilder request = initRequest(url(), RequestBuilder.GET, details, nonce, String.valueOf(timestamp));
		request.setHeader("fridge-timestamp", String.valueOf(timestamp));
		request.setCallback(new Callback(details, nonce, callback));
		try {
			request.send();
		} catch (RequestException ex) {
			log.warning("error sending request: " + ex.getMessage());
			callback.onError(ex);
		}
	}

	private class Callback extends FridgeRequest.Callback<Handler> {
		private Callback(SessionPlace details, String nonce, Handler handler) {
			super(details, nonce, handler, 204);
		}

		@Override
		protected void doCallbackSuccess(Handler handler, String message, Response response) {
			handler.onNonceReceived(response.getHeader("fridge-nonce-new"));
		}

		@Override
		protected void doCallbackFailure(Handler handler, Throwable ex) {
			log.info("nonce response failed: " + ex.getMessage());
			handler.onError(ex);
		}
	}

	static SafeUri url() {
		return UriUtils.fromSafeConstant(
				"/memphis/fridge/rest/nonce/generate/json"
		);
	}
}
