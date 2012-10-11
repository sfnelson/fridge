package memphis.fridge.dao;

import java.math.BigDecimal;
import java.util.logging.Logger;

import com.google.common.annotations.VisibleForTesting;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.persistence.EntityManager;
import memphis.fridge.domain.User;
import memphis.fridge.exceptions.FridgeException;
import memphis.fridge.exceptions.InsufficientFundsException;
import memphis.fridge.exceptions.InvalidUserException;
import memphis.fridge.utils.CryptUtils;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 29/09/12
 */
public class UserDAO {

	private final Logger log = Logger.getLogger(UserDAO.class.getName());

	@Inject
	Provider<EntityManager> em;

	@Inject
	FridgeDAO fridge;

	public void createGraduateUser(String username, String fullName, String email, String password) {
		User user = new User(username, fullName, email, password);
		user.setGrad(true);
		em.get().persist(user);
	}

	public void transferFunds(String fromUser, String toUser, BigDecimal amount) {
		throw new UnsupportedOperationException();
	}

	public User retrieveUser(String username) {
		return em.get().find(User.class, username);
	}

	public void checkValidUser(String username) {
		User user = retrieveUser(username);
		if (user == null || !user.isEnabled()) {
			throw new InvalidUserException(username);
		}
	}

	public void checkSufficientBalance(String username, BigDecimal required) {
		User user = retrieveUser(username);
		BigDecimal min = fridge.getMinimumUserBalance();
		if (user.isAdmin()) {
			min = fridge.getMinimumAdministratorBalance();
		}
		if (min.add(required).compareTo(user.getBalance()) > 0) {
			throw new InsufficientFundsException(username, user.getBalance());
		}
	}

	public void checkAdmin(String username) {
		User user = retrieveUser(username);
		if (!user.isAdmin()) {
			throw new FridgeException("user is not an admin");
		}
	}

	public void validateHMAC(String username, String hmac, Object... toValidate) throws FridgeException {
		String password = getPassword(username);

		if (password == null) {
			// user does not exist
			throw new FridgeException("Unable to validate request.");
		}

		if (hmac != null && hmac.equals(CryptUtils.sign(password, toValidate))) return;
		else throw new FridgeException("Unable to validate request.");
	}

	public String createHMAC(String username, Object... toSign) throws FridgeException {
		String password = getPassword(username);

		if (password == null) {
			// user does not exist
			throw new InvalidUserException(username);
		}

		return CryptUtils.sign(password, toSign);
	}

	public String createHMAC(String username, byte[] message) throws FridgeException {
		String password = getPassword(username);

		if (password == null) {
			// user does not exist
			throw new InvalidUserException(username);
		}

		return CryptUtils.sign(password, message);
	}

	@VisibleForTesting
	String getPassword(String username) {
		User user = em.get().find(User.class, username);

		if (user == null) return null;
		return user.getPassword();
	}

	public void removeFunds(User user, BigDecimal cost) {
		user = em.get().find(User.class, user.getUsername());
		user.setBalance(user.getBalance().subtract(cost));
		em.get().merge(user);
	}
}
