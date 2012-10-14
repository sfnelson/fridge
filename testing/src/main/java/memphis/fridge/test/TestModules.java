package memphis.fridge.test;

import java.lang.annotation.*;

/**
* Author: Stephen Nelson <stephen@sfnelson.org>
* Date: 14/10/12
*/
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface TestModules {
	TestModule[] value();
}
