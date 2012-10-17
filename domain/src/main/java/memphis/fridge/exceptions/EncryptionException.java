package memphis.fridge.exceptions;

/**
 * @author stephen
 */
public class EncryptionException extends FridgeException {
    public EncryptionException(String message, Throwable ex) {
        super(message, ex);
    }
}
