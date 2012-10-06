package memphis.fridge.server.io;

import memphis.fridge.dao.UserDAO;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 29/09/12
 */
public abstract class Response {

	private String hmac;

	protected Response(UserDAO userDAO, String username, Object... params) {
		this.hmac = userDAO.createHMAC(username, params);
	}

	public final void visitResponse(ResponseSerializer visitor) {
		visitParams(visitor);
		visitor.visitString("hmac", hmac);
	}

	protected abstract void visitParams(ResponseSerializer visitor);

}
