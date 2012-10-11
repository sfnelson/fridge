package memphis.fridge.server.services;

import javax.inject.Inject;
import memphis.fridge.dao.UserDAO;
import memphis.fridge.domain.User;
import memphis.fridge.protocol.Messages;
import memphis.fridge.server.ioc.RequireAuthenticated;

import static memphis.fridge.utils.CurrencyUtils.toCents;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 10/10/12
 */
public class AccountService {

	@Inject
	UserDAO users;

	@RequireAuthenticated
	public Messages.AccountResponse getAccountDetails(String username) {
		User user = users.retrieveUser(username);

		return Messages.AccountResponse.newBuilder()
				.setUsername(user.getUsername())
				.setFullName(user.getRealName())
				.setEmail(user.getEmail())
				.setBalance(toCents(user.getBalance()))
				.setIsAdmin(user.isAdmin())
				.setIsGrad(user.isGrad())
				.setIsEnabled(user.isEnabled())
				.build();
	}
}
