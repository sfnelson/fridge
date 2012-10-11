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
public abstract class FridgeRequest {

	@Inject
	CryptUtils crypt;

	protected RequestBuilder initRequest(SafeUri url, RequestBuilder.Method method, SessionPlace details, String nonce, String message) {
		RequestBuilder builder = new RequestBuilder(method, url.asString());
		String signature = crypt.sign(details.getSecret(), details.getUsername(), nonce, message);
		builder.setHeader("fridge-username", details.getUsername());
		builder.setHeader("fridge-nonce", nonce);
		builder.setHeader("fridge-signature", signature);
		return builder;
	}

	protected String validateResponse(SessionPlace details, String nonce, Response response) {
		String username = response.getHeader("fridge-username");
		String old_nonce = response.getHeader("fridge-nonce-old");
		String new_nonce = response.getHeader("fridge-nonce-new");
		String signature = response.getHeader("fridge-signature");
		String message = response.getText();
		if (username == null || !username.equals(details.getUsername())) {
			throw new RuntimeException("bad username");
		} else if (old_nonce == null || !old_nonce.equals(nonce)) {
			throw new RuntimeException("bad client nonce");
		} else if (new_nonce == null) {
			throw new RuntimeException("bad server nonce");
		} else if (signature == null) {
			throw new RuntimeException("bad signature");
		} else if (!crypt.verify(details.getSecret(), signature, username, old_nonce, new_nonce, message)) {
			throw new RuntimeException("account info failed verification");
		} else {
			return message;
		}
	}

	protected abstract class Callback<H> implements RequestCallback {
		protected final H handler;
		protected final SessionPlace details;
		protected final String nonce;
		protected final int success;

		protected Callback(SessionPlace details, String nonce, H handler, int success) {
			this.details = details;
			this.nonce = nonce;
			this.handler = handler;
			this.success = success;
		}

		public void onResponseReceived(Request request, Response response) {
			try {
				if (response.getStatusCode() == success) {
					try {
						String message = validateResponse(details, nonce, response);
						doCallbackSuccess(handler, message, response);
					} catch (IllegalArgumentException ex) {
						new RuntimeException("parsing nonce response failed.\n"
								+ ex.getMessage());
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
