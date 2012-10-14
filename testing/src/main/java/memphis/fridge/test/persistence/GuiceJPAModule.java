package memphis.fridge.test.persistence;

import com.google.inject.AbstractModule;
import com.google.inject.persist.jpa.JpaPersistModule;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 14/10/12
 */
public class GuiceJPAModule extends AbstractModule {

	private final String jpaModule;

	public GuiceJPAModule(String jpaModule) {
		this.jpaModule = jpaModule;
	}

	@Override
	protected void configure() {
		install(new JpaPersistModule(jpaModule));
	}
}
