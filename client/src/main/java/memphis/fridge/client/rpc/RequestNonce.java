package memphis.fridge.client.rpc;

import java.util.logging.Logger;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.http.client.*;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.safehtml.shared.UriUtils;

import javax.inject.Inject;

import memphis.fridge.client.utils.CryptUtils;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 30/09/12
 */
public class RequestNonce {

	private static final Logger log = Logger.getLogger("RequestNonce");

	public static interface NonceResponseHandler {
		public void onNonceReceived(String snonce);

		public void onError(Throwable exception);
	}

	@Inject
    CryptUtils crypt;

	@Inject
	Session session;

	public void requestNonce(String username, NonceResponseHandler request) {
		final String cnonce = crypt.generateNonceToken();
		final int timestamp = (int) (System.currentTimeMillis() / 1000);
		final String hmac = session.sign(cnonce, timestamp, username);

		SafeUri uri = createRequest(cnonce, timestamp, username, hmac);
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, uri.asString());
		builder.setCallback(new Callback(session, cnonce, request));
		try {
			builder.send();
		} catch (RequestException ex) {
			log.warning("error sending request: " + ex.getMessage());
		}
	}

	static SafeUri createRequest(String cnonce, int timestamp, String username, String hmac) {
		return UriUtils.fromSafeConstant(
				"/memphis/fridge/rest/generate_nonce/json"
						+ "?cnonce=" + UriUtils.encode(cnonce)
						+ "&timestamp=" + UriUtils.encode(String.valueOf(timestamp))
						+ "&username=" + UriUtils.encode(username)
						+ "&hmac=" + UriUtils.encode(hmac)
		);
	}

	private static class Callback implements RequestCallback {
		private final String cnonce;
		private final Session session;
		private final NonceResponseHandler handler;

		public Callback(Session session, String cnonce, NonceResponseHandler handler) {
			this.session = session;
			this.cnonce = cnonce;
			this.handler = handler;
		}

		public void onResponseReceived(Request request, Response response) {
			if (response.getStatusCode() == 200) {
				log.info("response: " + response.getStatusCode() + " " + response.getStatusText());
				try {
					Nonce n = JsonUtils.safeEval(response.getText());
					if (session.verify(n.getHMAC(), n.getServerNonce(), cnonce)) {
						handler.onNonceReceived(n.getServerNonce());
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

	private static class Nonce extends JavaScriptObject {

		protected Nonce() {
		}

		public native final String getServerNonce() /*-{
			return this.nonce;
		}-*/;

		public native final String getHMAC() /*-{
			return this.hmac;
		}-*/;
	}
}
