package memphis.fridge.server;

import com.google.inject.servlet.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.xml.ws.Response;
import memphis.fridge.protocol.Messages;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 28/09/12
 */
@Path("generate_nonce")
@RequestScoped
public class GenerateNonce {

	@Inject
	memphis.fridge.server.services.GenerateNonce service;

	@GET
	@Path("json")
	@Produces(MediaType.APPLICATION_JSON)
	public Response requestNonceJSON(Messages.NonceRequest request) {
		service.
		return service.generateNonce(clientNonce, timestamp, username, requestHMAC);
	}

	@GET
	@Path("xml")
	@Produces(MediaType.APPLICATION_XML)
	public HMACResponse requestNonceXML() {
		return service.generateNonce(clientNonce, timestamp, username, requestHMAC);
	}
}
