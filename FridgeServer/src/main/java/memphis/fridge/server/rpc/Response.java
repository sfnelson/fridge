package memphis.fridge.server.rpc;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 29/09/12
 */
public class Response<T> {

	private final T result;
	private final String hmac;

	public Response(T result, String hmac) {
		this.result = result;
		this.hmac = hmac;
	}

	public T getResult() {
		return result;
	}

	public String getHmac() {
		return hmac;
	}
}
