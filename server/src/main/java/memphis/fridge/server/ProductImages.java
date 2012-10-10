package memphis.fridge.server;

import java.io.ByteArrayInputStream;

import com.google.inject.servlet.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import memphis.fridge.server.services.Products;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 10/10/12
 */
@Path("images")
@RequestScoped
public class ProductImages {

	@Inject
	Products products;

	@QueryParam("product")
	String product;

	@PUT
	@Consumes("image/jpeg")
	public Response storeImage(byte[] image) {
		products.storeProductImage(product, image);
		return Response.noContent().build();
	}

	@GET
	@Produces("image/jpeg")
	public Response retrieveImage() {
		byte[] image = products.getProductImage(product);
		if (image == null) return Response.status(Response.Status.NOT_FOUND).build();
		return Response.ok(new ByteArrayInputStream(image)).build();
	}
}
