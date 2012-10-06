package memphis.fridge.server.io;

import javax.xml.bind.annotation.XmlElement;
import memphis.fridge.dao.UserDAO;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 29/09/12
 */
public abstract class HMACResponse extends ObjectResponse {

	@XmlElement(name = "hmac")
	public String hmac;

	protected HMACResponse() {
	}

	protected HMACResponse(UserDAO userDAO, String username, Object... params) {
		this.hmac = userDAO.createHMAC(username, params);
	}

	@Override
	public final void visit(ResponseSerializer.ObjectSerializer visitor) {
		visitParams(visitor);
		visitor.visitString("hmac", hmac);
	}

	protected abstract void visitParams(ResponseSerializer.ObjectSerializer visitor);

}
