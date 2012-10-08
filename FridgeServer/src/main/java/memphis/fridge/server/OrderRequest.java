package memphis.fridge.server;

import java.util.List;

import com.google.common.collect.Lists;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import memphis.fridge.server.io.HMACResponse;
import memphis.fridge.server.services.Purchase;
import memphis.fridge.utils.Pair;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import static memphis.fridge.utils.Pair.pair;

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
	public HMACResponse orderRequestJSON(JSONObject input) throws JSONException {
		String nonce = input.getString("nonce");
		String username = input.getString("username");
		String hmac = input.getString("hmac");
		JSONArray array = input.getJSONArray("items");
		List<Pair<String, Integer>> items = Lists.newArrayList();
		for (int i = 0; i < array.length(); i++) {
			JSONObject item = array.getJSONObject(i);
			String code = item.getString("code");
			int quantity = item.getInt("quantity");
			items.add(pair(code, quantity));
		}
		return service.purchase(nonce, username, items, hmac);
	}
}
