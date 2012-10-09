package memphis.fridge.server.services;

import memphis.fridge.server.io.HMACResponse;
import memphis.fridge.server.io.Request;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 30/09/12
 */
public interface RequestService {
	HMACResponse visitRequest(Request request);
}
