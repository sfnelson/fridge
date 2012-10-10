package memphis.fridge.server.services;

import javax.inject.Inject;
import javax.xml.bind.annotation.XmlRootElement;
import memphis.fridge.dao.UserDAO;
import memphis.fridge.domain.User;
import memphis.fridge.server.io.HMACResponse;
import memphis.fridge.server.io.ResponseSerializer;

import static memphis.fridge.utils.CurrencyUtils.toCents;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 10/10/12
 */
public class AccountService {

	@Inject
	UserDAO users;

	public HMACResponse getAccountDetails(String nonce, String username, String requestHMAC) {
		users.validateHMAC(username, requestHMAC, nonce, username);

		User user = users.retrieveUser(username);

		return new AccountDetailsResponse(users, user);
	}

	@XmlRootElement(name = "methodResponse")
	private static class AccountDetailsResponse extends HMACResponse {

		public String username;
		public String real_name;
		public String email;
		public int balance;
		public boolean is_admin;
		public boolean is_grad;
		public boolean is_enabled;

		protected AccountDetailsResponse() {
		}

		protected AccountDetailsResponse(UserDAO userDAO, User user) {
			super(userDAO, user.getUsername(), user.getUsername(), user.getRealName(), user.getEmail(),
					toCents(user.getBalance()), user.isAdmin(), user.isGrad(), user.isEnabled());
			this.username = user.getUsername();
			this.real_name = user.getRealName();
			this.email = user.getEmail();
			this.balance = toCents(user.getBalance());
			this.is_admin = user.isAdmin();
			this.is_grad = user.isGrad();
			this.is_enabled = user.isEnabled();
		}

		@Override
		protected void visitParams(ResponseSerializer.ObjectSerializer visitor) {
			visitor.visitString("username", username);
			visitor.visitString("real_name", real_name);
			visitor.visitString("email", email);
			visitor.visitInteger("balance", balance);
			visitor.visitBoolean("is_admin", is_admin);
			visitor.visitBoolean("is_grad", is_grad);
			visitor.visitBoolean("is_enabled", is_enabled);
		}
	}
}
