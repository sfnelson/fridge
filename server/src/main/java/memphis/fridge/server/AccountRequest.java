package memphis.fridge.server;

import com.google.inject.servlet.RequestScoped;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import memphis.fridge.server.io.HMACResponse;
import memphis.fridge.server.io.JSONObjectSerializer;
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

	@Inject
	Provider<JSONObjectSerializer> serializer;

	@QueryParam("nonce")
	String nonce;

	@QueryParam("username")
	String username;

	@QueryParam("hmac")
	String requestHMAC;

	@GET
	@Path("info/json")
	@Produces(MediaType.APPLICATION_JSON)
	public HMACResponse requestAccountDetailsJSON() {
		return service.getAccountDetails(nonce, username, requestHMAC);
	}

	@GET
	@Path("info/xml")
	@Produces(MediaType.APPLICATION_XML)
	public HMACResponse requestNonceXML() {
		return service.getAccountDetails(nonce, username, requestHMAC);
	}
}
