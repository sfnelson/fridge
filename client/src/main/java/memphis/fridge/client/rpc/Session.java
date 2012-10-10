package memphis.fridge.client.rpc;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;

import javax.inject.Inject;
import javax.inject.Provider;
import memphis.fridge.client.places.LoginPlace;
import memphis.fridge.client.utils.CryptUtils;

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

	public void logout() {
		this.username = null;
		this.password_md5 = null;
		this.nonce = null;
		goTo(LoginPlace.LOGIN);
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
