package memphis.fridge.exceptions;

import javax.ws.rs.core.Response;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 30/09/12
 */
public class InvalidProductException extends FridgeException {
	public InvalidProductException(String code) {
		super(Response.Status.GONE, "Invalid product: " + code);
	}
}
