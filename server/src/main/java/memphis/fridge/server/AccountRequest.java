package memphis.fridge.server;

import com.google.inject.servlet.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import memphis.fridge.protocol.Messages;
import memphis.fridge.server.services.AccountService;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 10/10/12
 */
@Path("account")
@RequestScoped
public class AccountRequest {

	@Inject
	AccountService service;

	@GET
	@Path("info/json")
	@Produces(MediaType.APPLICATION_JSON)
	public Messages.AccountResponse requestAccountDetails(Messages.AccountRequest input) {
		return service.getAccountDetails(input.getUsername());
	}
}
