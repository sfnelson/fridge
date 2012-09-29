package memphis.fridge.server.rpc;

import com.google.common.annotations.VisibleForTesting;
import javax.inject.Inject;
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

	public Response<String> generateNonce(String clientNonce, int timestamp, String username, String requestHMAC) {
		users.validateHMAC(username, requestHMAC, clientNonce, timestamp, username);

		Nonce nonce = nonces.generateNonce(clientNonce, timestamp);

		String hmac = users.createHMAC(username, nonce.getServerNonce(), nonce.getClientNonce());

		return new Response<String>(nonce.getServerNonce(), hmac);
	}
}
