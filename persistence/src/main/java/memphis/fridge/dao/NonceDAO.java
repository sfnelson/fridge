package memphis.fridge.dao;

import java.util.Date;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import memphis.fridge.domain.Nonce;
import memphis.fridge.domain.User;
import memphis.fridge.exceptions.AuthenticationException;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 29/09/12
 */
public class NonceDAO {

	@Inject
	Provider<EntityManager> em;

	@RequireTransaction
	public void consumeNonce(User user, String nonce, Date timestamp) {

		expireOldNonces();

		Date expires = Nonce.expires(timestamp);
		Date now = new Date();

		// check the timestamp
		if (expires.before(now)) {
			throw new AuthenticationException("timestamp too old");
		}

		// check client nonce doesn't already exist (replay)
		TypedQuery<Long> q = em.get().createNamedQuery("Nonce.findExisting", Long.class);
		q.setParameter("user", user);
		q.setParameter("nonce", nonce);
		q.setParameter("timestamp", timestamp);
		if (0 != q.getSingleResult()) {
			throw new AuthenticationException("nonce exists, possible replay");
		}

		// store nonce
		em.get().persist(new Nonce(user, nonce, timestamp));
	}

	@RequireTransaction
	private void expireOldNonces() {
		Query q = em.get().createNativeQuery(
				"DELETE FROM nonces WHERE timestamp < now() - interval '10 minutes';");
		q.executeUpdate();
	}
}
