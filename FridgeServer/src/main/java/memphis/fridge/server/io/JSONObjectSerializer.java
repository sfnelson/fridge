package memphis.fridge.server.io;

import java.util.logging.Logger;

import javax.inject.Inject;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 7/10/12
 */
public class JSONObjectSerializer implements ResponseSerializer.ObjectSerializer {

	@Inject
	private Logger log;

	JSONObject result = new JSONObject();

	public void visitString(String name, String value) {
		try {
			result.put(name, value);
		} catch (JSONException ex) {
			log.warning("could not append pair: " + name + ", " + value);
		}
	}

	public void visitInteger(String name, int value) {
		try {
			result.put(name, value);
		} catch (JSONException ex) {
			log.warning("could not append pair: " + name + ", " + value);
		}
	}

	public ResponseSerializer.ListSerializer visitList(String name) {
		JSONListSerializer list = new JSONListSerializer();
		try {
			result.put(name, list.get());
		} catch (JSONException ex) {
			log.warning("could not append pair: " + name + ", " + list.get());
		}
		return list;
	}

	public JSONObject get() {
		return result;
	}
}
