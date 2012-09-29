package memphis.fridge.dao;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Logger;

import com.google.common.annotations.VisibleForTesting;
import com.google.inject.persist.Transactional;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.xml.bind.DatatypeConverter;
import memphis.fridge.domain.User;
import memphis.fridge.exceptions.FridgeException;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 29/09/12
 */
public class UserDAO {

	private final Logger log = Logger.getLogger(UserDAO.class.getName());

	@Inject
	EntityManager em;

	@Transactional
	public void createGraduateUser(String username, String fullName, String email, String password) {
		User user = new User(username, fullName, email, password);
		user.setGrad(true);
		em.persist(user);
	}

	public User retrieveUser(String username) {
		return em.find(User.class, username);
	}

	public void validateHMAC(String username, String hmac, Object... toValidate) throws FridgeException {

		String password = getPassword(username);

		if (password == null) {
			// user does not exist
			throw new FridgeException(1, "Unable to validate request.");
		}

		if (hmac != null && hmac.equals(sign(password, toValidate))) return;
		else throw new FridgeException(2, "Unable to validate request.");
	}

	public String createHMAC(String username, Object... toSign) throws FridgeException {
		String password = getPassword(username);

		if (password == null) {
			// user does not exist
			throw new FridgeException(1, "Unable to validate request.");
		}

		return sign(password, toSign);
	}

	@VisibleForTesting
	String getPassword(String username) {
		User user = em.find(User.class, username);

		if (user == null) return null;
		return user.getPassword();
	}

	@VisibleForTesting
	String sign(String password, Object... toSign) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < toSign.length; i++) {
			if (i > 0) sb.append(",");
			sb.append(toSign[i].toString());
		}

		try {
			SecretKeySpec key = new SecretKeySpec(password.getBytes("CP1252"), "HmacMD5");
			Mac mac = Mac.getInstance("HmacMD5");
			mac.init(key);
			byte[] hmacBytes = mac.doFinal(sb.toString().getBytes("CP1252"));
			return DatatypeConverter.printHexBinary(hmacBytes).toLowerCase();
		} catch (UnsupportedEncodingException e) {
			log.severe(e.getMessage());
		} catch (NoSuchAlgorithmException e) {
			log.severe(e.getMessage());
		} catch (InvalidKeyException e) {
			log.severe(e.getMessage());
		}

		throw new FridgeException(1, "Unrecoverable signing error.");
	}
}
