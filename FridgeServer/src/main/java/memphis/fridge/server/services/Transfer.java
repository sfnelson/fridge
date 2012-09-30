package memphis.fridge.server.services;

import java.math.BigDecimal;

import javax.inject.Inject;
import memphis.fridge.dao.UserDAO;
import memphis.fridge.exceptions.InvalidAmountException;
import memphis.fridge.server.io.Response;
import memphis.fridge.server.io.ResponseSerializer;
import memphis.fridge.utils.CurrencyUtils;

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

		int balance = CurrencyUtils.toCents(users.retrieveUser(fromUser).getBalance());
		return new TransferResponse(balance, users.createHMAC(fromUser, snonce, balance));
	}

	private static class TransferResponse implements Response {
		int balance;
		String hmac;

		private TransferResponse(int balance, String hmac) {
			this.balance = balance;
			this.hmac = hmac;
		}

		public void visitResponse(ResponseSerializer visitor) {
			visitor.visitInteger("balance", balance);
			visitor.visitString("hmac", hmac);
		}
	}
}
