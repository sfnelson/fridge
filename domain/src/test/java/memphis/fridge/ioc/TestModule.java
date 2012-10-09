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
public class TestModule extends AbstractModule {
	@Override
	protected void configure() {
		install(new JpaPersistModule("TestFridgeDB"));
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
