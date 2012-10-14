package memphis.fridge.client.rpc;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.safehtml.shared.SafeUri;

import javax.inject.Inject;
import memphis.fridge.client.places.SessionPlace;
import memphis.fridge.client.utils.CryptUtils;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 12/10/12
 */
abstract class AbstractSignedRequest {

	public static final String CONTENT_TYPE = "Content-Type";
	public static final String APPLICATION_JSON = "application/json";

	private static final String USERNAME = "fridge-username";
	private static final String NONCE = "fridge-nonce";
	private static final String TIMESTAMP = "fridge-timestamp";
	private static final String SIGNATURE = "fridge-signature";

	@Inject
	private CryptUtils crypt;

	protected SessionPlace details;
	protected String username;
	protected String nonce;
	protected String timestamp;

	protected void init(SessionPlace details) {
		this.details = details;
		this.username = details.getUsername();
		this.nonce = crypt.generateNonceToken();
		this.timestamp = String.valueOf((int) (System.currentTimeMillis() / 1000));
	}

	protected RequestBuilder request(RequestBuilder.Method method, SafeUri uri, String verb, String message, RequestCallback callback) {
		RequestBuilder builder = new RequestBuilder(method, uri.asString());
		String signature = crypt.sign(details.getSecret(), username, nonce, timestamp, verb, message);
		builder.setHeader(USERNAME, username);
		builder.setHeader(NONCE, nonce);
		builder.setHeader(TIMESTAMP, timestamp);
		builder.setHeader(SIGNATURE, signature);
		builder.setHeader(CONTENT_TYPE, APPLICATION_JSON);
		builder.setRequestData(message);
		builder.setCallback(callback);
		return builder;
	}

	protected String validateResponse(Response response) {
		String server_username = response.getHeader(USERNAME);
		String server_nonce = response.getHeader(NONCE);
		String server_time = response.getHeader(TIMESTAMP);
		String signature = response.getHeader(SIGNATURE);
		String message = response.getText();
		if (server_username == null || !server_username.equals(username)) {
			throw new RuntimeException("bad username");
		} else if (server_nonce == null || !server_nonce.equals(nonce)) {
			throw new RuntimeException("bad nonce");
		} else if (server_time == null || !server_time.equals(timestamp)) {
			throw new RuntimeException("bad server nonce");
		} else if (signature == null) {
			throw new RuntimeException("bad signature");
		} else if (!crypt.verify(details.getSecret(), signature, username, nonce, timestamp, message)) {
			throw new RuntimeException("account info failed verification");
		} else {
			return message;
		}
	}

	static abstract class Callback<H> implements RequestCallback {
		protected final AbstractSignedRequest req;
		protected final H handler;
		protected final int success;

		protected Callback(AbstractSignedRequest req, int success, H handler) {
			this.req = req;
			this.handler = handler;
			this.success = success;
		}

		public void onResponseReceived(Request request, Response response) {
			try {
				if (response.getStatusCode() == success) {
					try {
						String message = req.validateResponse(response);
						doCallbackSuccess(handler, message, response);
					} catch (IllegalArgumentException ex) {
						new RuntimeException("parsing nonce response failed.\n" + ex.getMessage());
					}
				} else {
					throw new RuntimeException("unexpected response from server: "
							+ response.getStatusCode() + " " + response.getStatusText());
				}
			} catch (Exception ex) {
				doCallbackFailure(handler, ex);
			}
		}

		public void onError(Request request, Throwable exception) {
			doCallbackFailure(handler, exception);
		}

		protected abstract void doCallbackSuccess(H handler, String message, Response response);

		protected abstract void doCallbackFailure(H handler, Throwable ex);
	}
}
