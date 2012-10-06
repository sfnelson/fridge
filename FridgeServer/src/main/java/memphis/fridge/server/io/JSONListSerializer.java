package memphis.fridge.server.io;

import org.codehaus.jettison.json.JSONArray;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 7/10/12
 */
public class JSONListSerializer implements ResponseSerializer.ListSerializer {

	JSONArray array = new JSONArray();

	public ResponseSerializer.ObjectSerializer visitObject() {
		JSONObjectSerializer obj = new JSONObjectSerializer();
		array.put(obj.get());
		return obj;
	}

	public ResponseSerializer.ListSerializer visitSublist() {
		JSONListSerializer list = new JSONListSerializer();
		array.put(list.get());
		return list;
	}

	public JSONArray get() {
		return array;
	}
}
