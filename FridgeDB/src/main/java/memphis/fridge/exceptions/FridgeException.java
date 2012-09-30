package memphis.fridge.exceptions;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 28/09/12
 */
public class FridgeException extends RuntimeException {

	private int code;

	protected FridgeException() {
	}

	protected FridgeException(String message, Throwable ex) {
		super(message, ex);
	}

	public FridgeException(int code, String message) {
		super(message);
		this.code = code;
	}

	public int getCode() {
		return code;
	}
}
