package memphis.fridge.exceptions;

import javax.ws.rs.core.Response;
import memphis.fridge.domain.Product;

public class ProductExistsException extends FridgeException {
	public ProductExistsException(Product product) {
		super(Response.Status.BAD_REQUEST, "Product exists");
	}
}
