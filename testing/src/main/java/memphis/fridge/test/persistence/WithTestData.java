package memphis.fridge.test.persistence;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 14/10/12
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface WithTestData {
	Class<? extends TestDataProvider> value();
}
