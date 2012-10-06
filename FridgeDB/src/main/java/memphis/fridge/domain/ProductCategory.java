package memphis.fridge.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 30/09/12
 */
@Entity(name = "category")
public class ProductCategory {

	@Id
	@Column(name = "category_id")
	private int id;

	@NotNull
	@Column(name = "display_sequence", nullable = false)
	private int displaySequence;

	@NotNull
	@Column(name = "title", nullable = false, length = 25)
	private String title;

	public ProductCategory() {
	}

	public ProductCategory(String title, int displaySequence) {
		this.title = title;
		this.displaySequence = displaySequence;
	}

	public int getDisplaySequence() {
		return displaySequence;
	}

	public String getTitle() {
		return title;
	}
}
