package memphis.fridge.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 29/09/12
 */
@Entity
@Table(name = "nonces")
@NamedQueries({
		@NamedQuery(name = "Nonce.findExisting",
				query = "SELECT count(n) FROM Nonce n WHERE n.id.user = :user "
						+ "AND n.id.nonce = :nonce AND n.id.timestamp = :timestamp")
})
public class Nonce {

	public static final int VALID_PERIOD = 600; // 10 minutes

	@Embeddable
	protected static class NonceId implements Serializable {
		@NotNull
		@ManyToOne(optional = false)
		@JoinColumn(name = "username", referencedColumnName = "username")
		private User user;

		@NotNull
		@Size(max = 20)
		@Column(name = "nonce", nullable = false, length = 20)
		private String nonce;

		@NotNull
		@Temporal(TemporalType.TIMESTAMP)
		@Column(name = "timestamp", nullable = false)
		private Date timestamp;

		public NonceId() {
		}

		public NonceId(User user, String nonce, Date timestamp) {
			this.user = user;
			this.nonce = nonce;
			this.timestamp = timestamp;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (!(o instanceof NonceId)) return false;

			NonceId nonceId = (NonceId) o;

			if (!nonce.equals(nonceId.nonce)) return false;
			if (!timestamp.equals(nonceId.timestamp)) return false;
			if (!user.equals(nonceId.user)) return false;

			return true;
		}

		@Override
		public int hashCode() {
			int result = user.hashCode();
			result = 31 * result + nonce.hashCode();
			result = 31 * result + timestamp.hashCode();
			return result;
		}
	}

	@EmbeddedId
	@NotNull
	private NonceId id;

	public Nonce(User user, String nonce, Date timestamp) {
		this.id = new NonceId(user, nonce, timestamp);
	}

	Nonce() {
	}
}
