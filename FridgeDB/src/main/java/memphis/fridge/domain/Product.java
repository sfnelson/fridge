package memphis.fridge.domain;

import java.math.BigDecimal;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 30/09/12
 */
@Entity
@Table(name = "product")
@NamedQueries({
		@NamedQuery(name = "Products.findEnabled",
				query = "SELECT p FROM Product p WHERE p.enabled = true")
})
public class Product {

	@Id
	@NotNull
	@Column(name = "product_code", nullable = false)
	private String productCode;

	@NotNull
	@Column(name = "description", nullable = false)
	private String description;

	@NotNull
	@Column(name = "cost", nullable = false, precision = 11, scale = 2)
	private BigDecimal cost;

	@NotNull
	@Column(name = "markup", nullable = false, precision = 4, scale = 1)
	private BigDecimal markup;

	@NotNull
	@Column(name = "enabled", nullable = false)
	private boolean enabled;

	@NotNull
	@ManyToOne(optional = false)
	@JoinColumn(name = "category_id", referencedColumnName = "category_id")
	private ProductCategory category;

	@NotNull
	@Column(name = "in_stock", nullable = false)
	private int inStock;

	@NotNull
	@Column(name = "stock_low_mark", nullable = false)
	private int stockLowMark;

	Product() {
	}

	public Product(String productCode, String description, BigDecimal cost, BigDecimal markup, ProductCategory category) {
		this.productCode = productCode;
		this.description = description;
		this.cost = cost;
		this.markup = markup;
		enabled = true;
		this.category = category;
		inStock = 0;
		stockLowMark = 0;
	}

	public String getProductCode() {
		return productCode;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public BigDecimal getCost() {
		return cost;
	}

	public void setCost(BigDecimal cost) {
		this.cost = cost;
	}

	public BigDecimal getMarkup() {
		return markup;
	}

	public void setMarkup(BigDecimal markup) {
		this.markup = markup;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public ProductCategory getCategory() {
		return category;
	}

	public void setCategory(ProductCategory category) {
		this.category = category;
	}

	public int getInStock() {
		return inStock;
	}

	public void setInStock(int inStock) {
		this.inStock = inStock;
	}

	public int getStockLowMark() {
		return stockLowMark;
	}

	public void setStockLowMark(int stockLowMark) {
		this.stockLowMark = stockLowMark;
	}
}
