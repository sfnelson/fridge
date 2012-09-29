package memphis.fridge.server.rpc;

import java.util.Map;

import com.google.common.collect.Maps;
import javax.inject.Inject;
import memphis.fridge.exceptions.FridgeException;
import memphis.fridge.server.Database;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 28/09/12
 */
public class GenerateNonce {

	/**
	 * Maximum age of a nonce, in seconds
	 */
	public static final long MAX_AGE = 600;

	@Inject
	private Database db;

	public Map<String, String> generateNonce(String clientNonce, long timestamp, String username, String requestHMAC) {
		Map<String, String> response = Maps.newHashMap();

		// flush old nonces
		db.deleteOldNonces();

		// check the timestamp
		long now = System.currentTimeMillis() / 1000;
		if (timestamp + MAX_AGE < now) {
			throw new FridgeException(1, "Timestamp too old.");
		}

		// Check client nonce (namely that it is not already associated with a server nonce)
		int count = db.countNonceOccurances(clientNonce);
		if (count != 0) {
			throw new FridgeException(2, "Invalid client nonce.");
		}

		// Check the HMAC from the client
		/*
		db.authenticateUser(username, )

			$authenticated_balance = authenticate_user(array($cnonce, $timestamp, $username), $username, $request_hmac, $password_md5, $is_grad, $is_interfridge);
			if (!is_int($authenticated_balance)) {
				//Authentication failed.
				return error(3, "Error authenticating for nonce: $authenticated_balance");
			}

			//Generate a nonce
			$snonce = make_nonce();
			DBQuery("INSERT INTO nonces (snonce, cnonce) VALUES('" . pg_escape_string($snonce) . "', '" . pg_escape_string($cnonce) . "')");

			//Calculate the HMAC
			$response_hmac = sign($password_md5, array($snonce, $cnonce));
			return array('nonce' => $snonce, 'hmac' => $response_hmac);
		*/

		return response;
	}
}
