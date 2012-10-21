package memphis.fridge.exceptions;

import javax.ws.rs.core.Response;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 11/10/12
 */
public class AuthenticationException extends FridgeException {
	public AuthenticationException(String reason) {
		super(Response.Status.UNAUTHORIZED, String.format("authentication failed (%s)", reason));
	}
}
