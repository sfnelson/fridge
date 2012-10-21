package memphis.fridge.exceptions;

import javax.ws.rs.core.Response;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 2/10/12
 */
public class InsufficientStockException extends FridgeException {
	public InsufficientStockException() {
		super(Response.Status.PRECONDITION_FAILED, "Insufficient Stock");
	}
}
