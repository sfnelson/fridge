package memphis.fridge.exceptions;

import javax.ws.rs.core.Response;

/**
 * @author stephen
 */
public class EncryptionException extends FridgeException {
    public EncryptionException(String message, Throwable ex) {
        super(Response.Status.INTERNAL_SERVER_ERROR, message, ex);
    }
}
