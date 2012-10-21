package memphis.fridge.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.annotation.Nullable;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 9/10/12
 */
@Entity
@Table(name = "user_credit_log")
public class CreditLogEntry {

	public static CreditLogEntry createPurchase(User user, BigDecimal amount) {
		CreditLogId id = new CreditLogId(user, new Date());
		return new CreditLogEntry(id, amount, Type.PURCHASE, null, null);
	}

	public static CreditLogEntry createTransferFrom(User user, BigDecimal amount, User from) {
		CreditLogId id = new CreditLogId(user, new Date());
		return new CreditLogEntry(id, amount, Type.XFER_IN, from, null);
	}

	public static CreditLogEntry createTransferTo(User user, BigDecimal amount, User to) {
		CreditLogId id = new CreditLogId(user, new Date());
		return new CreditLogEntry(id, amount, Type.XFER_OUT, null, to);
	}

	public static CreditLogEntry createTopup(User user, BigDecimal amount) {
		CreditLogId id = new CreditLogId(user, new Date());
		return new CreditLogEntry(id, amount, Type.CREDIT, null, null);
	}

	public enum Type {
		PURCHASE("PURCHASE"),
		CREDIT("CREDIT"),
		XFER_OUT("XFER-OUT"),
		XFER_IN("XFER-IN"),
		ADMIN("ADMIN");

		private final String value;

		Type(String value) {
			this.value = value;
		}

		public String toString() {
			return this.name().replace('_', '-');
		}

		public static Type fromString(String value) {
			return Type.valueOf(value.replace('-', '_'));
		}
	}

	@Embeddable
	protected static class CreditLogId implements Serializable {
		@NotNull
		@ManyToOne(optional = false)
		@JoinColumn(name = "username", referencedColumnName = "username")
		private User user;

		@NotNull
		@Temporal(TemporalType.TIMESTAMP)
		@Column(name = "date_time", nullable = false)
		private Date timestamp;

		public CreditLogId() {
		}

		public CreditLogId(User user, Date timestamp) {
			this.user = user;
			this.timestamp = timestamp;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (!(o instanceof CreditLogId)) return false;

			CreditLogId that = (CreditLogId) o;

			if (!timestamp.equals(that.timestamp)) return false;
			if (!user.equals(that.user)) return false;

			return true;
		}

		@Override
		public int hashCode() {
			int result = user.hashCode();
			result = 31 * result + timestamp.hashCode();
			return result;
		}
	}

	@EmbeddedId
	private CreditLogId id;

	@NotNull
	@Column(name = "amount", precision = 9, scale = 2)
	private BigDecimal amount;

	@NotNull
	@Size(max = 10)
	@Column(name = "transaction_type", length = 10, nullable = false)
	private String type;

	@Nullable
	@ManyToOne(optional = true)
	@JoinColumn(name = "transfer_received_from", referencedColumnName = "username")
	private User transferReceivedFrom;

	@Nullable
	@ManyToOne(optional = true)
	@JoinColumn(name = "transfer_sent_to", referencedColumnName = "username")
	private User transferSentTo;

	CreditLogEntry() {
	}

	CreditLogEntry(CreditLogId id, BigDecimal amount, Type type, User from, User to) {
		this.id = id;
		this.amount = amount;
		this.type = type.toString();
		this.transferReceivedFrom = from;
		this.transferSentTo = to;
	}
}
