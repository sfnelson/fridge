package memphis.fridge.exceptions;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 11/10/12
 */
public class AuthenticationException extends FridgeException {

	public AuthenticationException() {
	}

	public AuthenticationException(String message) {
		super(message);
	}
}
