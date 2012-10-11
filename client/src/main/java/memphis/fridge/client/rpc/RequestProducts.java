package memphis.fridge.client.rpc;

import java.util.AbstractList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.http.client.*;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.safehtml.shared.UriUtils;
import com.google.gwt.user.client.Window;

import javax.annotation.Nullable;
import javax.inject.Inject;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 7/10/12
 */
public class RequestProducts extends FridgeRequest {

	private static final Logger log = Logger.getLogger("products");

	public interface Handler {
		void productsReady(List<? extends Product> products);
	}

	@Inject
	Session session;

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

	private class Callback implements RequestCallback {
		private final Handler handler;

		public Callback(Handler handler) {
			this.handler = handler;
		}

		public void onResponseReceived(Request request, final Response response) {
			if (response.getStatusCode() == 200) {
				final JsArray<Product> products = Product.parse(response.getText());
				handler.productsReady(new AbstractList<Product>() {
					@Override
					public Product get(int index) {
						return products.get(index);
					}

					@Override
					public int size() {
						return products.length();
					}

					public Iterator<Product> iterator() {
						final int size = products.length();
						return new Iterator<Product>() {
							int position = 0;

							public boolean hasNext() {
								return position < size;
							}

							public Product next() {
								return products.get(position++);
							}

							public void remove() {
								throw new UnsupportedOperationException();
							}
						};
					}
				});
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
