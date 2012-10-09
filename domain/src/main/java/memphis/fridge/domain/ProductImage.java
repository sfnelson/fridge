package memphis.fridge.domain;

import java.io.Serializable;

import javax.persistence.*;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 10/10/12
 */
@Entity
@Table(name = "product_images")
public class ProductImage implements Serializable {

	@Id
	@Column(name = "product")
	String product_code;

	@MapsId
	@OneToOne
	@JoinColumn(name = "product")
	Product product;

	byte[] image;

	public ProductImage() {
	}

	public ProductImage(Product product, byte[] image) {
		this.product_code = product.getProductCode();
		this.product = product;
		this.image = image;
	}

	public Product getProduct() {
		return product;
	}

	public byte[] getImage() {
		return image;
	}

	public void setImage(byte[] image) {
		this.image = image;
	}
}
