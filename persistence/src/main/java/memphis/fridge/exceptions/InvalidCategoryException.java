package memphis.fridge.exceptions;

import javax.ws.rs.core.Response;

public class InvalidCategoryException extends FridgeException {
	public InvalidCategoryException(int id) {
		super(Response.Status.BAD_REQUEST, "Invalid category (" + id + ")");
	}
}
