package memphis.fridge.exceptions;

import java.math.BigDecimal;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 30/09/12
 */
public class InsufficientFundsException extends FridgeException {

	public InsufficientFundsException(String user, BigDecimal balance) {
		super(402, String.format("Insufficient funds")); // payment required
	}
}
