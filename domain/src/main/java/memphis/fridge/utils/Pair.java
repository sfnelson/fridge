package memphis.fridge.utils;

import javax.validation.constraints.NotNull;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 30/09/12
 */
public class Pair<K, V> {

	public static <K, V> Pair<K, V> pair(K key, V value) {
		return new Pair<K, V>(key, value);
	}

	@NotNull
	K key;
	@NotNull
	V value;

	public Pair(@NotNull K key, @NotNull V value) {
		this.key = key;
		this.value = value;
	}

	@NotNull
	public K getKey() {
		return key;
	}

	@NotNull
	public V getValue() {
		return value;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Pair pair = (Pair) o;

		if (!key.equals(pair.key)) return false;
		if (!value.equals(pair.value)) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = key.hashCode();
		result = 31 * result + value.hashCode();
		return result;
	}

	@Override
	public String toString() {
		return String.format("(%s, %s)", key.toString(), value.toString());
	}
}
