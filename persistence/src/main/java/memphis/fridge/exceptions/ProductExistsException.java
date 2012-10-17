package memphis.fridge.exceptions;

import memphis.fridge.domain.Product;

public class ProductExistsException extends FridgeException {

    private final Product product;

    public ProductExistsException(Product product) {
        this.product = product;
    }
}
