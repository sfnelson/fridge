package memphis.fridge.exceptions;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 30/09/12
 */
public class InvalidUserException extends FridgeException {

	private String username;

	public InvalidUserException(String username) {
		this.username = username;
	}
}
