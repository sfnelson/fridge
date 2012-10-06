package memphis.fridge.dao;

import java.math.BigDecimal;
import java.util.logging.Logger;

import com.google.common.annotations.VisibleForTesting;
import com.google.inject.persist.Transactional;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import memphis.fridge.domain.User;
import memphis.fridge.exceptions.FridgeException;
import memphis.fridge.utils.CryptUtils;

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

	@Transactional
	public void transferFunds(String fromUser, String toUser, BigDecimal amount) {
		throw new UnsupportedOperationException();
	}

	public User retrieveUser(String username) {
		return em.find(User.class, username);
	}

	public void checkValidUser(String username) {
		throw new UnsupportedOperationException();
	}

	public void checkSufficientBalance(String username, BigDecimal required) {
		throw new UnsupportedOperationException();
	}

	public void validateHMAC(String username, String hmac, Object... toValidate) throws FridgeException {

		String password = getPassword(username);

		if (password == null) {
			// user does not exist
			throw new FridgeException(1, "Unable to validate request.");
		}

		if (hmac != null && hmac.equals(CryptUtils.sign(password, toValidate))) return;
		else throw new FridgeException(2, "Unable to validate request.");
	}

	public String createHMAC(String username, Object... toSign) throws FridgeException {
		String password = getPassword(username);

		if (password == null) {
			// user does not exist
			throw new FridgeException(1, "Unable to validate request.");
		}

		return CryptUtils.sign(password, toSign);
	}

	@VisibleForTesting
	String getPassword(String username) {
		User user = em.find(User.class, username);

		if (user == null) return null;
		return user.getPassword();
	}

	public void removeFunds(User user, BigDecimal costGrad) {
		throw new UnsupportedOperationException();
	}
}
