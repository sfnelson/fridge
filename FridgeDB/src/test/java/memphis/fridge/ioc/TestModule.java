package memphis.fridge.ioc;

import com.google.inject.AbstractModule;
import com.google.inject.persist.jpa.JpaPersistModule;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 29/09/12
 */
public class TestModule extends AbstractModule {
	@Override
	protected void configure() {
		install(new JpaPersistModule("TestFridgeDB"));
	}
}
