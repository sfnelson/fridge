package memphis.fridge.utils;

import java.util.Set;

import javax.validation.ConstraintViolation;

import static org.junit.Assert.assertEquals;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 9/10/12
 */
public class ValidationUtils {

	public static <T> void validate(Set<ConstraintViolation<T>> result, int expected) {
		for (ConstraintViolation<T> v : result) {
			System.err.printf("%s.%s %s (%s)\n", v.getLeafBean().getClass().getSimpleName(), v.getPropertyPath(),
					v.getMessage(), v.getInvalidValue());
		}
		assertEquals(expected, result.size());
	}
}
