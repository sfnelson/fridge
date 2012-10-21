package memphis.fridge.server.services;

import java.math.BigDecimal;

import com.google.inject.persist.Transactional;
import javax.inject.Inject;
import memphis.fridge.dao.CreditLogDAO;
import memphis.fridge.dao.UserDAO;
import memphis.fridge.domain.User;
import memphis.fridge.exceptions.AuthenticationException;
import memphis.fridge.exceptions.InvalidAmountException;
import memphis.fridge.protocol.Messages;
import memphis.fridge.server.ioc.RequireAdmin;
import memphis.fridge.server.ioc.RequireAuthenticated;
import memphis.fridge.server.ioc.SessionState;
import memphis.fridge.utils.CurrencyUtils;

import static memphis.fridge.utils.CurrencyUtils.fromCents;
import static memphis.fridge.utils.CurrencyUtils.toCents;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 10/10/12
 */
public class Users {

	@Inject
	SessionState session;

	@Inject
	UserDAO users;

	@Inject
	CreditLogDAO creditLog;

	@RequireAuthenticated
	public Messages.AccountResponse getAccountDetails() {
		User user = session.getUser();

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

	@RequireAdmin
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

	@Transactional
	@RequireAuthenticated
	public Messages.TransactionResponse topup(Messages.TopupRequest request) {
		User user = session.getUser();
		BigDecimal amount = fromCents(request.getAmount());

		creditLog.createTopup(user, amount);
		users.addFunds(user, amount);

		user = users.retrieveUser(user.getUsername());
		return Messages.TransactionResponse.newBuilder()
				.setBalance(toCents(user.getBalance()))
				.setCost(request.getAmount())
				.build();
	}

	@Transactional
	@RequireAuthenticated
	public Messages.TransactionResponse transfer(Messages.TransferRequestOrBuilder request) {
		String fromUser = request.getFromUser();
		String toUser = request.getToUser();
		int amount = request.getAmount();

		users.checkValidUser(request.getFromUser());
		User from = users.retrieveUser(request.getFromUser());

		users.checkValidUser(request.getToUser());
		User to = users.retrieveUser(request.getToUser());

		checkMeOrAdmin(fromUser);

		if (amount < 0) throw new InvalidAmountException("Negative transfers forbidden");
		if (amount > 0) {
			BigDecimal toTransfer = CurrencyUtils.fromCents(amount);
			users.checkSufficientBalance(fromUser, toTransfer);

			creditLog.createTransfer(from, to, toTransfer);
			users.transferFunds(from, to, toTransfer);
		}

		User user = users.retrieveUser(fromUser);
		return Messages.TransactionResponse.newBuilder()
				.setBalance(toCents(user.getBalance()))
				.setCost(amount).build();
	}

	void checkMeOrAdmin(String user) {
		if (session.getUser().getUsername().equals(user)) return;
		if (session.getUser().isAdmin()) return;
		throw new AuthenticationException(String.format("You are not authorized to access %s's account", user));
	}
}
