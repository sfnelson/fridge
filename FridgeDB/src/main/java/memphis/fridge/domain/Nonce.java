package memphis.fridge.domain;

import java.util.Date;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 29/09/12
 */
@Entity
@Table(name = "nonces")
@NamedQueries({
		@NamedQuery(name = "Nonce.findExisting",
				query = "SELECT count(n) FROM Nonce n WHERE n.clientNonce = :cnonce")
})
public class Nonce {

	public static final int VALID_PERIOD = 600; // 10 minutes

	@Id
	@NotNull
	@Column(name = "snonce")
	private String serverNonce;

	@NotNull
	@Column(name = "cnonce")
	private String clientNonce;

	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_at")
	private Date createAt;

	public Nonce(String serverNonce, String clientNonce, Date createAt) {
		this.serverNonce = serverNonce;
		this.clientNonce = clientNonce;
		this.createAt = createAt;
	}

	Nonce() {
	}

	/**
	 * A nonce is only valid for a short time after being generated (currently 10 minutes).
	 */
	public Date getCreatedAt() {
		return createAt;
	}

	/**
	 * The server nonce is a unique token that can be consumed by a client to perform an action on the server.
	 */
	public String getServerNonce() {
		return serverNonce;
	}

	/**
	 * The client nonce is a unique token provided and signed by the client when requesting a server nonce.
	 * The one-to-one mapping between cnonce and snonce prevents unauthenticated parties from filling the
	 * nonce table by replaying nonce requests.
	 */
	public String getClientNonce() {
		return clientNonce;
	}

}
