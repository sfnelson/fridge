package memphis.fridge.client.rpc;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 30/09/12
 */
public interface NonceRequest {
	void nonceReady(String nonce);
}
