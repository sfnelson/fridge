package memphis.fridge.server;

import com.google.inject.servlet.RequestScoped;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import memphis.fridge.server.io.JSONListSerializer;
import memphis.fridge.server.services.GetProducts;
import org.codehaus.jettison.json.JSONArray;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 7/10/12
 */
@Path("get_products")
@RequestScoped
public class GetProductsRequest {
	@Inject
	GetProducts service;

	@Inject
	Provider<JSONListSerializer> serializer;

	@QueryParam("username")
	String username;

	@GET
	@Path("json")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONArray getProductsJSON() {
		JSONListSerializer s = serializer.get();
		service.getProducts(username).visit(s);
		return s.get();
	}
}
