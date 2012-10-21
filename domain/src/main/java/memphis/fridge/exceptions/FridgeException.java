package memphis.fridge.exceptions;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 28/09/12
 */
public class FridgeException extends WebApplicationException {

	private Throwable cause;

	protected FridgeException(Response.Status status, String message, Throwable ex) {
		this(status, message);
		this.cause = ex;
	}

	protected FridgeException(int status, String message) {
		super(Response.status(status)
				.entity(message)
				.type(MediaType.TEXT_PLAIN)
				.build());
	}

	public FridgeException(Response.Status status, String message) {
		super(Response.status(status)
				.entity(message)
				.type(MediaType.TEXT_PLAIN)
				.build());
	}

	public Throwable getCause() {
		return cause;
	}
}
