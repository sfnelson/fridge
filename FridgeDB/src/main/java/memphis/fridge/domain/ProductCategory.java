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

    public ProductCategory(int id, int displaySequence, String title) {
        this.id = id;
        this.displaySequence = displaySequence;
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public int getDisplaySequence() {
        return displaySequence;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProductCategory category = (ProductCategory) o;

        if (displaySequence != category.displaySequence) return false;
        if (id != category.id) return false;
        if (!title.equals(category.title)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + displaySequence;
        result = 31 * result + title.hashCode();
        return result;
    }
}
