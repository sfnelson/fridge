package memphis.fridge.exceptions;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 30/09/12
 */
public class InvalidProductException extends FridgeException {

    private final String code;

    public InvalidProductException(String code) {
        this.code = code;
    }
}
