package memphis.fridge.server.ioc;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;
import com.google.inject.persist.Transactional;
import com.google.inject.servlet.RequestScoped;
import javax.inject.Inject;
import memphis.fridge.dao.NonceDAO;
import memphis.fridge.dao.UserDAO;
import memphis.fridge.domain.User;
import memphis.fridge.exceptions.AuthenticationException;
import memphis.fridge.exceptions.InvalidUserException;
import memphis.fridge.server.io.Signed;
import memphis.fridge.utils.CryptUtils;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 11/10/12
 */
@RequestScoped
public class SessionState {

	private static final String USERNAME = "fridge-username";
	private static final String NONCE = "fridge-nonce";
	private static final String TIMESTAMP = "fridge-timestamp";
	private static final String SIGNATURE = "fridge-signature";

	@Inject
	UserDAO users;

	@Inject
	NonceDAO nonceStore;

	private boolean isAuthenticated;
	private User user;
	private String username;
	private String nonce;
	private String timestamp;

	@Transactional
	public void authenticate(Signed verb, Map<String, List<String>> params, String message) {
		// not signed unless it has username, nonce, timestamp, verb, and signature
		if (!params.containsKey(USERNAME) || params.get(USERNAME).isEmpty())
			throw new AuthenticationException("invalid username");
		if (!params.containsKey(NONCE) || params.get(NONCE).isEmpty())
			throw new AuthenticationException("invalid nonce");
		if (!params.containsKey(TIMESTAMP) || params.get(TIMESTAMP).isEmpty())
			throw new AuthenticationException("invalid timestamp");
		if (verb == null || verb.value() == null || verb.value().isEmpty())
			throw new AuthenticationException("invalid username");
		if (!params.containsKey(SIGNATURE) || params.get(SIGNATURE).isEmpty())
			throw new AuthenticationException("invalid signature");

		String username = params.get(USERNAME).get(0);
		String nonce = params.get(NONCE).get(0);
		String timestamp = params.get(TIMESTAMP).get(0);
		String signature = params.get(SIGNATURE).get(0);

		assert (user != null);
		assert signature != null;
		assert username != null;
		assert nonce != null;
		assert timestamp != null;
		assert verb != null && verb.value() != null;
		assert message != null;

		try {
			users.checkValidUser(username);
		} catch (InvalidUserException ex) {
			throw new AuthenticationException("invalid user");
		}

		User user = users.retrieveUser(username);
		Date realTimestamp = new Date(Long.valueOf(timestamp) * 1000);

		// throw an exception if the signature is invalid
		if (!signature.equals(CryptUtils.sign(user.getPassword(), username, nonce, timestamp, verb.value(), message))) {
			throw new AuthenticationException("encryption error");
		}

		// consume the current nonce to prevent replay attacks
		nonceStore.consumeNonce(user, nonce, realTimestamp);

		// user is authenticated at this point
		this.isAuthenticated = true;
		this.user = user;
		this.nonce = nonce;
		this.timestamp = timestamp;
	}

	public Map<String, String> sign(String message) {
		assert (user != null);
		assert message != null;

		String username = user.getUsername();
		String signature = CryptUtils.sign(user.getPassword(), username, nonce, timestamp, message);

		Map<String, String> params = Maps.newHashMapWithExpectedSize(4);
		params.put(USERNAME, username);
		params.put(NONCE, nonce);
		params.put(TIMESTAMP, timestamp);
		params.put(SIGNATURE, signature);
		return params;
	}

	public boolean isAuthenticated() {
		if (isAuthenticated) assert user != null;
		return isAuthenticated;
	}

	public boolean isGrad() {
		assert (user != null);
		return user.isGrad();
	}

	public boolean isAdmin() {
		assert (user != null);
		return user.isAdmin();
	}

	public boolean isEnabled() {
		assert (user != null);
		return user.isEnabled();
	}

	public boolean isUserOrAdmin(String username) {
		assert (user != null);
		assert (username != null);
		return user.isAdmin() || user.getUsername().equals(username);
	}

	public User getUser() {
		return user;
	}
}
