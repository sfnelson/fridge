package memphis.fridge.client.rpc;

import java.util.logging.Logger;

import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.http.client.*;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.safehtml.shared.UriUtils;

import javax.inject.Inject;
import memphis.fridge.client.places.SessionPlace;
import memphis.fridge.client.utils.CryptUtils;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 10/10/12
 */
public class RequestAccountInfo {

	private static final Logger log = Logger.getLogger("account-info");

	public static interface Handler {
		public void onAccountReady(Account account);

		public void onError(Throwable exception);
	}

	@Inject
	CryptUtils crypt;

	public void requestAccountInfo(SessionPlace details, String nonce, Handler callback) {
		final String hmac = crypt.sign(details.getSecret(), nonce, details.getUsername());

		SafeUri uri = createRequest(nonce, details.getUsername(), hmac);
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, uri.asString());
		builder.setCallback(new Callback(details, nonce, callback));
		try {
			builder.send();
		} catch (RequestException ex) {
			log.warning("error sending request: " + ex.getMessage());
		}
	}

	private class Callback implements RequestCallback {
		private final SessionPlace details;
		private final String nonce;
		private final Handler handler;

		public Callback(SessionPlace details, String nonce, Handler handler) {
			this.details = details;
			this.nonce = nonce;
			this.handler = handler;
		}

		public void onResponseReceived(Request request, Response response) {
			if (response.getStatusCode() == 200) {
				log.info("response: " + response.getStatusCode() + " " + response.getStatusText());
				try {
					Account a = JsonUtils.safeEval(response.getText());
					if (crypt.verify(details.getSecret(), a.getHMAC(), a.getUsername(), a.getRealName(), a.getEmail(),
							a.getBalance(), a.isAdmin(), a.isGrad(), a.isEnabled())) {
						handler.onAccountReady(a);
					} else {
						handler.onError(new RuntimeException("account info failed verification"));
					}
				} catch (IllegalArgumentException ex) {
					log.warning("parsing account info response failed.\n" + ex.getMessage());
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

	static SafeUri createRequest(String nonce, String username, String hmac) {
		return UriUtils.fromSafeConstant(
				"/memphis/fridge/rest/account/info/json"
						+ "?nonce=" + UriUtils.encode(nonce)
						+ "&username=" + UriUtils.encode(username)
						+ "&hmac=" + UriUtils.encode(hmac)
		);
	}
}
