package memphis.fridge.exceptions;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 30/09/12
 */
public class InvalidAmountException extends FridgeException {

	public InvalidAmountException() {
	}

	public InvalidAmountException(ArithmeticException ex) {
		super(ex.getMessage(), ex);
	}
}
