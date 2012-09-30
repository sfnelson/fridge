package memphis.fridge.dao;

import java.util.Date;

import com.google.inject.persist.Transactional;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import memphis.fridge.domain.Nonce;
import memphis.fridge.exceptions.FridgeException;
import memphis.fridge.utils.CryptUtils;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 29/09/12
 */
public class NonceDAO {

	@Inject
	EntityManager em;

	@Transactional
	public Nonce generateNonce(String cnonce, int timestamp) {

		expireOldNonces();

		// check the timestamp
		long now = System.currentTimeMillis() / 1000;
		if (timestamp + Nonce.VALID_PERIOD < now) {
			throw new FridgeException(1, "Timestamp too old.");
		}

		// check client nonce doesn't already exist (replay)
		TypedQuery<Long> q = em.createNamedQuery("Nonce.findExisting", Long.class);
		q.setParameter("cnonce", cnonce);
		if (0 != q.getSingleResult()) {
			throw new FridgeException(2, "Invalid client nonce.");
		}

		// generate new server nonce
		String snonce = CryptUtils.generateNonceToken();

		// create and store nonce
		Nonce nonce = new Nonce(snonce, cnonce, new Date());
		em.persist(nonce);

		return nonce;
	}

	private void expireOldNonces() {
		Query q = em.createNativeQuery(
				"DELETE FROM nonces WHERE created_at > now() - interval '10 minutes'");
		q.executeUpdate();
	}
}
