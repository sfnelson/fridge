package memphis.fridge.server;

import com.google.inject.servlet.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import memphis.fridge.protocol.Messages;
import memphis.fridge.server.services.GetProducts;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 7/10/12
 */
@Path("products")
@RequestScoped
public class ProductsRequest {

	@Inject
	GetProducts service;

	@QueryParam("username")
	String username;

	@GET
	@Path("list/json")
	@Produces(MediaType.APPLICATION_JSON)
	public Messages.StockResponse getProductsJSON() {
		return service.getProducts(username);
	}
}
