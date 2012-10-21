package memphis.fridge.exceptions;

import javax.ws.rs.core.Response;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 30/09/12
 */
public class InvalidUserException extends FridgeException {
	public InvalidUserException(String username) {
		super(Response.Status.BAD_REQUEST, username + " is not a valid user");
	}
}
