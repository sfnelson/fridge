package memphis.fridge.client.events;

import memphis.fridge.client.rpc.Messages;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 10/10/12
 */
public interface ProductHandler {
    void productSelected(Messages.Stock p);
}
