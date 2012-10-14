package memphis.fridge.ioc;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.persist.jpa.JpaPersistModule;
import javax.inject.Singleton;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 29/09/12
 */
public class TestModuleWithValidator extends AbstractModule {

	private final String jpaModule;

	public TestModuleWithValidator(String jpaModule) {
		this.jpaModule = jpaModule;
	}

	@Override
	protected void configure() {
		install(new JpaPersistModule(jpaModule));
	}

	@Provides
	@Singleton
	ValidatorFactory createValidatorFactory() {
		return Validation.buildDefaultValidatorFactory();
	}

	@Provides
	Validator createValidator(ValidatorFactory factory) {
		return factory.getValidator();
	}
}
