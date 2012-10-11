package memphis.fridge.server;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import memphis.fridge.protocol.Messages;
import memphis.fridge.server.services.Purchase;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 9/10/12
 */
@Path("purchase")
public class OrderRequest {
	@Inject
	Purchase service;

	@POST
	@Path("json")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Messages.TransactionResponse orderRequest(Messages.PurchaseRequest request) {
		return service.purchase(request);
	}
}
