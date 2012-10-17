package memphis.fridge.domain;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.NotNull;
import org.junit.Before;
import org.junit.Test;

import static memphis.fridge.utils.ValidationUtils.validate;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 9/10/12
 */
public class UserTest {

    private static ValidatorFactory factory = Validation.buildDefaultValidatorFactory();

	private Validator validator;

    @Before
    public void setUp() {
        validator = factory.getValidator();
    }

	class TestValidation {
		@NotNull
		Object foo;
	}

    @Test
    public void testValidation() throws Exception {
        TestValidation v = new TestValidation();
        validate(validator.validate(v), 1);
        v.foo = new Object();
        validate(validator.validate(v), 0);
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
