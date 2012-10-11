package memphis.fridge.server.services;

import java.math.BigDecimal;

import com.google.inject.persist.Transactional;
import javax.inject.Inject;
import memphis.fridge.dao.UserDAO;
import memphis.fridge.domain.User;
import memphis.fridge.exceptions.InvalidAmountException;
import memphis.fridge.protocol.Messages;
import memphis.fridge.server.ioc.RequireAuthenticated;
import memphis.fridge.utils.CurrencyUtils;

import static memphis.fridge.utils.CurrencyUtils.toCents;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 30/09/12
 */
public class Transfer {

	@Inject
	UserDAO users;

	@Transactional
	@RequireAuthenticated
	public Messages.TransactionResponse transfer(Messages.TransferRequestOrBuilder request) {
		String fromUser = request.getFromUser();
		String toUser = request.getToUser();
		int amount = request.getAmount();

		users.checkValidUser(request.getToUser());

		if (amount < 0) throw new InvalidAmountException();
		if (amount > 0) {
			BigDecimal toTransfer = CurrencyUtils.fromCents(amount);
			users.checkSufficientBalance(fromUser, toTransfer);

			users.transferFunds(fromUser, toUser, toTransfer);
		}

		User user = users.retrieveUser(fromUser);
		return Messages.TransactionResponse.newBuilder()
				.setBalance(toCents(user.getBalance()))
				.setCost(amount).build();
	}
}
