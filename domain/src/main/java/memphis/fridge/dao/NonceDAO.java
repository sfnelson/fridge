package memphis.fridge.dao;

import java.util.Date;

import com.google.inject.persist.Transactional;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import memphis.fridge.domain.Nonce;
import memphis.fridge.exceptions.AuthenticationException;
import memphis.fridge.exceptions.FridgeException;
import memphis.fridge.utils.CryptUtils;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 29/09/12
 */
public class NonceDAO {

	@Inject
	Provider<EntityManager> em;

	@Transactional
	public Nonce generateNonce(String cnonce, int timestamp) {

		expireOldNonces();

		// check the timestamp
		long now = System.currentTimeMillis() / 1000;
		if (timestamp + Nonce.VALID_PERIOD < now) {
			throw new FridgeException("Timestamp too old.");
		}

		// check client nonce doesn't already exist (replay)
		TypedQuery<Long> q = em.get().createNamedQuery("Nonce.findExisting", Long.class);
		q.setParameter("cnonce", cnonce);
		if (0 != q.getSingleResult()) {
			throw new FridgeException("Invalid client nonce.");
		}

		// generate new server nonce
		String snonce = CryptUtils.generateNonceToken();

		// create and store nonce
		Nonce nonce = new Nonce(snonce, cnonce, new Date());
		em.get().persist(nonce);

		return nonce;
	}

	@Transactional
	public String consumeNonce(String nonce) {

		expireOldNonces();

		Nonce record = em.get().find(Nonce.class, nonce);

		if (record == null) {
			throw new AuthenticationException("nonce is invalid/consumed");
		}

		em.get().remove(record);

		String next = CryptUtils.generateNonceToken();
		em.get().persist(new Nonce(next, nonce, new Date()));
		return next;
	}

	private void expireOldNonces() {
		Query q = em.get().createNativeQuery(
				"DELETE FROM nonces WHERE created_at > now() - interval '10 minutes'");
		q.executeUpdate();
	}
}
