package memphis.fridge.client.rpc;

import java.util.List;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;

import javax.inject.Inject;
import javax.inject.Provider;

import memphis.fridge.client.utils.CryptUtils;
import memphis.fridge.client.places.LoginPlace;
import memphis.fridge.client.places.PurchasePlace;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 30/09/12
 */
public class Session {

	private String username;
	private String password_md5;
	private String nonce;

	@Inject
    CryptUtils crypt;

	@Inject
	PlaceController place;

	@Inject
	Provider<RequestNonce> nonceRequest;

	@Inject
	Provider<RequestPurchase> purchaseRequest;

	@Inject
	Provider<Scheduler> scheduler;

	public boolean isLoggedIn() {
		return username != null && password_md5 != null;
	}

	public void login(final String username, final String password) {
		this.username = username;
		this.password_md5 = crypt.md5(password);
		requestNonce(new RequestNonce.NonceResponseHandler() {
			public void onNonceReceived(String nonce) {
				Session.this.nonce = nonce;
				goTo(new PurchasePlace(username));
			}

			public void onError(Throwable exception) {
				Session.this.username = null;
				Session.this.password_md5 = null;
				Session.this.nonce = null;
				goTo(LoginPlace.LOGIN);
			}
		});
	}

	public void logout() {
		this.username = null;
		this.password_md5 = null;
		this.nonce = null;
		goTo(LoginPlace.LOGIN);
	}

	public void requestNonce(RequestNonce.NonceResponseHandler callback) {
		if (nonce != null) {
			String nonce = this.nonce;
			this.nonce = null;
			callback.onNonceReceived(nonce);
		} else {
			nonceRequest.get().requestNonce(username, callback);
		}
	}

	public void placeOrder(final List<PurchaseEntry> content, final RequestPurchase.OrderResponseHandler callback) {
		requestNonce(new RequestNonce.NonceResponseHandler() {
			public void onNonceReceived(String nonce) {
				purchaseRequest.get().requestOrder(nonce, username, content, callback);
			}

			public void onError(Throwable exception) {
				callback.onError(exception);
			}
		});
	}

	String sign(Object... request) {
		if (password_md5 == null) return null;
		return crypt.sign(password_md5, request);
	}

	boolean verify(String signature, Object... params) {
		if (password_md5 == null) return false;
		return crypt.verify(password_md5, signature, params);
	}

	void goTo(final Place target) {
		scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
			public void execute() {
				place.goTo(target);
			}
		});
	}
}
