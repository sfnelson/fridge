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
import memphis.fridge.server.services.GenerateNonce;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 28/09/12
 */
@Path("generate_nonce")
@RequestScoped
public class GenerateNonceRequest {

	@Inject
	GenerateNonce service;

	@Inject
	Provider<JSONObjectSerializer> serializer;

	@QueryParam("cnonce")
	String clientNonce;

	@QueryParam("timestamp")
	int timestamp;

	@QueryParam("username")
	String username;

	@QueryParam("hmac")
	String requestHMAC;

	@GET
	@Path("json")
	@Produces(MediaType.APPLICATION_JSON)
	public HMACResponse requestNonceJSON() {
		return service.generateNonce(clientNonce, timestamp, username, requestHMAC);
	}

	@GET
	@Path("xml")
	@Produces(MediaType.APPLICATION_XML)
	public HMACResponse requestNonceXML() {
		return service.generateNonce(clientNonce, timestamp, username, requestHMAC);
	}
}
