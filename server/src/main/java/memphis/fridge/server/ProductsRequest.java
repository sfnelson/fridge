package memphis.fridge.server;

import com.google.inject.servlet.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import memphis.fridge.protocol.Messages;
import memphis.fridge.server.ioc.SessionState;
import memphis.fridge.server.services.Products;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 7/10/12
 */
@Path("products")
@RequestScoped
public class ProductsRequest {

    @Inject
    SessionState session;

	@Inject
    Products service;

	@GET
	@Path("list/json")
	@Produces(MediaType.APPLICATION_JSON)
	public Messages.StockResponse getProductsJSON() {
        if (!session.isAuthenticated())
            return service.getProducts();
        return service.getProductsAuthenticated();
	}
}
