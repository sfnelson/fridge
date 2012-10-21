package memphis.fridge.exceptions;

import javax.ws.rs.core.Response;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 30/09/12
 */
public class InvalidAmountException extends FridgeException {
	public InvalidAmountException(String message) {
		super(Response.Status.BAD_REQUEST, message);
	}

	public InvalidAmountException(ArithmeticException ex) {
		super(Response.Status.BAD_REQUEST, ex.getMessage(), ex);
	}
}
