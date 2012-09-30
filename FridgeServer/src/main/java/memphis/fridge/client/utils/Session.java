package memphis.fridge.client.utils;

import java.util.LinkedList;
import java.util.Queue;

import javax.inject.Inject;
import javax.inject.Provider;
import memphis.fridge.client.rpc.NonceRequest;
import memphis.fridge.client.rpc.RequestNonce;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 30/09/12
 */
public class Session {

	private String username;
	private String password_md5;
	private String nonce;

	private Queue<NonceRequest> awaiting = new LinkedList<NonceRequest>();

	@Inject
	CryptUtils crypt;

	@Inject
	Provider<RequestNonce> request;

	public void login(String username, String password) {
		this.username = username;
		this.password_md5 = crypt.md5(password);
	}

	public void logout() {
		this.username = null;
		this.password_md5 = null;
		this.nonce = null;
	}

	public String sign(Object... request) {
		return crypt.sign(password_md5, request);
	}

	public void getNonce(NonceRequest request) {
		awaiting.add(request);
		doRequest();
	}

	public void nonceReady(String nonce) {
		if (username == null) return;
		if (awaiting.isEmpty()) {
			this.nonce = nonce;
		} else {
			NonceRequest rq = awaiting.poll();
			rq.nonceReady(nonce);
		}
	}

	private void doRequest() {
		if (nonce != null) {
			nonceReady(nonce);
		} else {
			this.request.get().requestNonce(username);
		}
	}
}
