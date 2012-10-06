package memphis.fridge.server.services;

import com.google.common.annotations.VisibleForTesting;
import javax.inject.Inject;
import memphis.fridge.dao.NonceDAO;
import memphis.fridge.dao.UserDAO;
import memphis.fridge.domain.Nonce;
import memphis.fridge.server.io.Response;
import memphis.fridge.server.io.ResponseSerializer;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 28/09/12
 */
public class GenerateNonce {

	@VisibleForTesting
	@Inject
	NonceDAO nonces;

	@VisibleForTesting
	@Inject
	UserDAO users;

	public Response generateNonce(String clientNonce, int timestamp, String username, String requestHMAC) {
		users.validateHMAC(username, requestHMAC, clientNonce, timestamp, username);

		Nonce nonce = nonces.generateNonce(clientNonce, timestamp);

		return new NonceResponse(username, nonce.getServerNonce(), nonce.getClientNonce());
	}

	private class NonceResponse extends Response {
		String snonce;

		NonceResponse(String username, String snonce, String cnonce) {
			super(users, username, snonce, cnonce);
			this.snonce = snonce;
		}

		public void visitParams(ResponseSerializer visitor) {
			visitor.visitString("snonce", snonce);
		}
	}
}
