package memphis.fridge.server.services;

import java.math.BigDecimal;

import javax.inject.Inject;
import memphis.fridge.dao.UserDAO;
import memphis.fridge.domain.User;
import memphis.fridge.exceptions.InvalidAmountException;
import memphis.fridge.server.io.Response;
import memphis.fridge.server.io.ResponseSerializer;
import memphis.fridge.utils.CurrencyUtils;

import static memphis.fridge.utils.CurrencyUtils.toCents;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 30/09/12
 */
public class Transfer {

	@Inject
	UserDAO users;

	public Response transfer(String snonce, String fromUser, String toUser, int amount, String hmac) {
		users.validateHMAC(fromUser, hmac, snonce, fromUser, toUser, amount);
		users.checkValidUser(toUser);

		if (amount < 0) throw new InvalidAmountException();
		if (amount > 0) {
			BigDecimal toTransfer = CurrencyUtils.fromCents(amount);
			users.checkSufficientBalance(fromUser, toTransfer);

			users.transferFunds(fromUser, toUser, toTransfer);
		}

		User user = users.retrieveUser(fromUser);
		BigDecimal balance = user.getBalance();
		return new TransferResponse(fromUser, snonce, toCents(balance));
	}

	private class TransferResponse extends Response {
		int balance;

		private TransferResponse(String username, String snonce, int balance) {
			super(users, username, snonce, balance);
			this.balance = balance;
		}

		public void visitParams(ResponseSerializer visitor) {
			visitor.visitInteger("balance", balance);
		}
	}
}
