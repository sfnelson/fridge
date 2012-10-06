package memphis.fridge.client.rpc;

import com.google.gwt.http.client.*;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.safehtml.shared.UriUtils;
import com.google.gwt.user.client.Window;

import javax.inject.Inject;
import memphis.fridge.client.utils.CryptUtils;
import memphis.fridge.client.utils.Session;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 30/09/12
 */
public class RequestNonce implements RequestCallback {

	@Inject
	CryptUtils crypt;

	@Inject
	Session session;

	public void requestNonce(String username) {
		String cnonce = crypt.generateNonceToken();
		int timestamp = (int) (System.currentTimeMillis() / 1000);
		String hmac = session.sign(cnonce, timestamp, username);

		SafeUri uri = createRequest(cnonce, timestamp, username, hmac);
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, uri.asString());
		builder.setCallback(this);
		try {
			builder.send();
		} catch (RequestException ex) {
			// todo error handling
			Window.alert("Error sending request: " + ex.getMessage());
		}
	}

	public void onError(Request request, Throwable exception) {
	}

	public void onResponseReceived(Request request, Response response) {

	}

	public static SafeUri createRequest(String cnonce, int timestamp, String username, String hmac) {
		return UriUtils.fromSafeConstant(
				"/fridge/rest/generate_nonce/xml"
						+ "?cnonce=" + UriUtils.encode(cnonce)
						+ "&timestamp=" + UriUtils.encode(String.valueOf(timestamp))
						+ "&username=" + UriUtils.encode(username)
						+ "&hmac=" + UriUtils.encode(hmac)
		);
	}
}
