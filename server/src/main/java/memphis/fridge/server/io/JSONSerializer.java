package memphis.fridge.server.io;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 7/10/12
 */
public class JSONSerializer implements ResponseSerializer<JSONObject, JSONArray> {

	public JSONObject visitObject(ObjectResponse response) {
		JSONObjectSerializer s = new JSONObjectSerializer();
		response.visit(s);
		return s.get();
	}

	public <T extends Response> JSONArray visitList(ListResponse<T> response) {
		JSONListSerializer s = new JSONListSerializer();
		response.visit(s);
		return s.get();
	}
}
