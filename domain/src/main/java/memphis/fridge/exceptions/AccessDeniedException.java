package memphis.fridge.exceptions;

public class AccessDeniedException extends FridgeException {

    private final String username;

    public AccessDeniedException(String username) {
        this.username = username;
    }
}
