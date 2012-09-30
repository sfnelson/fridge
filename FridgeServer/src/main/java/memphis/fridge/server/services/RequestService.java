package memphis.fridge.server.services;

import memphis.fridge.server.io.Request;
import memphis.fridge.server.io.Response;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 30/09/12
 */
public interface RequestService {
	Response visitRequest(Request request);
}
