package memphis.fridge.client.rpc;

import java.util.List;
import java.util.logging.Logger;

import com.google.gwt.http.client.*;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.safehtml.shared.UriUtils;
import com.google.gwt.user.client.Window;

import javax.annotation.Nullable;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 7/10/12
 */
public class ProductRequest extends AbstractSignedRequest {

	private static final Logger log = Logger.getLogger("products");

	public interface Handler {
		void productsReady(List<? extends Messages.Stock> products);
	}

	public void requestProducts(String username, Handler handler) {
		SafeUri uri = createRequest(username);
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, uri.asString());
		builder.setCallback(new Callback(handler));
		try {
			builder.send();
		} catch (RequestException ex) {
			// todo error handling
			Window.alert("Error sending request: " + ex.getMessage());
		}
	}

	@Override
	Logger getLog() {
		return log;
	}

	private class Callback implements RequestCallback {
		private final Handler handler;

		public Callback(Handler handler) {
			this.handler = handler;
		}

		public void onResponseReceived(Request request, final Response response) {
			if (response.getStatusCode() == 200) {
				Messages.StockResponse stock = Messages.parseStockResponse(response.getText());
				handler.productsReady(stock.getStock());
			} else {
				log.info("request returned status code " + response.getStatusCode());
			}
		}

		public void onError(Request request, Throwable exception) {
			log.warning(exception.getMessage());
		}
	}

	private static final SafeUri createRequest(@Nullable String username) {
		return UriUtils.fromSafeConstant(
				"/memphis/fridge/rest/products/list/json" +
						(username != null ? "?username=" + UriUtils.encode(username) : "")
		);
	}
}
