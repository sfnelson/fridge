package memphis.fridge.test;

import java.lang.annotation.*;

import com.google.inject.Module;

/**
* Author: Stephen Nelson <stephen@sfnelson.org>
* Date: 14/10/12
*/
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface TestModule {
	Class<? extends Module> value();

	String[] args() default {};
}
