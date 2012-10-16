package memphis.fridge.client.events;

import memphis.fridge.client.rpc.Account;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 10/10/12
 */
public interface AccountHandler {
	void accountAvailable(Account a);
}
