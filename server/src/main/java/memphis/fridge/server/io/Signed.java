package memphis.fridge.server.io;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 12/10/12
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER, ElementType.FIELD})
public @interface Signed {
	/**
	 * @return the action/verb that this message should be authenticated against
	 */
	String value();
}
