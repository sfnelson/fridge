package memphis.fridge.server;

import com.google.inject.servlet.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import memphis.fridge.protocol.Messages;
import memphis.fridge.server.io.Signed;
import memphis.fridge.server.ioc.SessionState;
import memphis.fridge.server.services.Users;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 10/10/12
 */
@Path("account")
@RequestScoped
public class AccountRequest {

	@Inject
	SessionState session;

	@Inject
	Users service;

	@POST
	@Path("info/json")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Messages.AccountResponse requestAccountDetails(@Signed("account-request") Messages.AccountRequest input) {
		if (session.getUser().getUsername().equals(input.getUsername())) {
			return service.getAccountDetails();
		} else {
			return service.getAccountDetails(input.getUsername());
		}
	}

	@POST
	@Path("topup/json")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Messages.TransactionResponse topupAccount(@Signed("topup-request") Messages.TopupRequest input) {
		return service.topup(input);
	}

	@POST
	@Path("transfer/json")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Messages.TransactionResponse transferFunds(@Signed("transfer-request") Messages.TransferRequest input) {
		return service.transfer(input);
	}
}
