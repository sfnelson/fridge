package memphis.fridge.test;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 14/10/12
 */
@Entity
public class MapEntry {

	@Id
	private String name;

	private String value;

	public MapEntry() {
	}

	public MapEntry(String name, String value) {
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
