package memphis.fridge.server;

import java.util.Map;

import com.google.inject.servlet.RequestScoped;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.*;
import memphis.fridge.server.ioc.SessionState;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 28/09/12
 */
@Path("nonce")
@RequestScoped
public class GenerateNonce {

	@Inject
	Provider<SessionState> session;

	@GET
	@Path("generate/json")
	@Produces(MediaType.APPLICATION_JSON)
	public Response requestNonceJSON(@Context HttpHeaders headers) {
		MultivaluedMap<String, String> headersMap = headers.getRequestHeaders();

		session.get().nonceRequest(headersMap);

		Response.ResponseBuilder builder = Response.noContent();
		Map<String, String> responseHeaders = session.get().sign("");
		for (Map.Entry<String, String> e : responseHeaders.entrySet()) {
			builder.header(e.getKey(), e.getValue());
		}
		return builder.build();
	}
}
