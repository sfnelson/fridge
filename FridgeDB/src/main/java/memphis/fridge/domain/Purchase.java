package memphis.fridge.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 30/09/12
 */
@Entity(name = "purchases")
public class Purchase {

	@Embeddable
	protected class PurchaseId implements Serializable {
		@NotNull
		@ManyToOne(optional = false)
		@JoinColumn(name = "username", referencedColumnName = "username")
		private User user;

		@NotNull
		@ManyToOne(optional = false)
		@JoinColumn(name = "product_code", referencedColumnName = "product_code")
		private Product product;

		@NotNull
		@Temporal(TemporalType.TIMESTAMP)
		@Column(name = "date_time", nullable = false)
		private Date timestamp;

		public PurchaseId() {
		}

		public PurchaseId(User user, Product product, Date timestamp) {
			this.user = user;
			this.product = product;
			this.timestamp = timestamp;
		}
	}

	@EmbeddedId
	private PurchaseId id;

	@NotNull
	@Column(name = "purchase_quantity", nullable = false)
	private int purchaseQuantity;

	@NotNull
	@Column(name = "amount", precision = 11, scale = 2)
	private BigDecimal amount;

	@NotNull
	@Column(name = "surplus", precision = 11, scale = 2)
	private BigDecimal surplus;

	Purchase() {
	}

	public Purchase(User user, Product product, Date timestamp, int purchaseQuantity, BigDecimal amount, BigDecimal surplus) {
		this.id = new PurchaseId(user, product, timestamp);
		this.purchaseQuantity = purchaseQuantity;
		this.amount = amount;
		this.surplus = surplus;
	}

	public User getUser() {
		return id.user;
	}

	public Product getProduct() {
		return id.product;
	}

	public Date getTimestamp() {
		return id.timestamp;
	}

	public int getPurchaseQuantity() {
		return purchaseQuantity;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public BigDecimal getSurplus() {
		return surplus;
	}
}
