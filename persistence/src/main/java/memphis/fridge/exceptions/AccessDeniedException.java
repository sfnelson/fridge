package memphis.fridge.exceptions;

import javax.ws.rs.core.Response;
import memphis.fridge.domain.User;

public class AccessDeniedException extends FridgeException {
	public AccessDeniedException(User user) {
		super(Response.Status.FORBIDDEN, String.format("%s is not authorized to make this request", user.getUsername()));
	}
}
