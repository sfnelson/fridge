package memphis.fridge.server.services;

import com.google.common.annotations.VisibleForTesting;
import javax.inject.Inject;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import memphis.fridge.dao.NonceDAO;
import memphis.fridge.dao.UserDAO;
import memphis.fridge.domain.Nonce;

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

	public HMACResponse generateNonce(String clientNonce, int timestamp, String username, String requestHMAC) {
		users.validateHMAC(username, requestHMAC, clientNonce, timestamp, username);

		Nonce nonce = nonces.generateNonce(clientNonce, timestamp);

		return new NonceResponse(users, username, nonce.getServerNonce(), nonce.getClientNonce());
	}

	@XmlRootElement(name = "methodResponse")
	private static class NonceResponse extends HMACResponse {

		@XmlElement(name = "nonce")
		public String snonce;

		public NonceResponse() {
		}

		NonceResponse(UserDAO dao, String username, String snonce, String cnonce) {
			super(dao, username, snonce, cnonce);
			this.snonce = snonce;
		}

		@Override
		protected void visitParams(ResponseSerializer.ObjectSerializer visitor) {
			visitor.visitString("snonce", snonce);
		}
	}
}
