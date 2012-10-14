package memphis.fridge.domain;

import javax.inject.Inject;
import javax.validation.Validator;
import javax.validation.constraints.NotNull;
import memphis.fridge.ioc.TestModuleWithValidator;
import memphis.fridge.test.GuiceTestRunner;
import memphis.fridge.test.TestModule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static memphis.fridge.utils.ValidationUtils.validate;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 9/10/12
 */
@RunWith(GuiceTestRunner.class)
@TestModule(value = TestModuleWithValidator.class, args = "FridgeTestDB")
public class UserTest {

	@Inject
	Validator validator;

	class TestValidation {
		@NotNull
		Object foo;
	}

	@Test
	public void testCreateUserNoArgs() throws Exception {
		User user = new User();
		validate(validator.validate(user), 3);
	}

	@Test
	public void testCreateUserNameTooLong() throws Exception {
		User user = new User("USERNAME_IS_WAY_TOO_LONG", "PASSWORD", "Real Name", "email@addre.ss");
		validate(validator.validate(user), 1);
	}

	@Test
	public void testCreateUserPassTooLong() throws Exception {
		User user = new User("USERNAME", "PASSWORD_IS_TOO_LONG_TO_FIT_BLAH_BLAH", "Real Name", "email@addre.ss");
		validate(validator.validate(user), 1);
	}

	@Test
	public void testCreateUser() throws Exception {
		User user = new User("USERNAME", "PASSWORD", "Real Name", "email@addre.ss");
		validate(validator.validate(user), 0);
	}


}
