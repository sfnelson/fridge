package memphis.fridge.exceptions;

public class InvalidCategoryException extends FridgeException {

    private final int id;

    public InvalidCategoryException(int id) {
        this.id = id;
    }
}
