package memphis.fridge.client.utils;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 30/09/12
 */
public interface CryptUtils {

	String generateNonceToken();

	String sign(String secret, Object... toSign);

	String md5(String message);

	boolean verify(String secret, String signature, Object... params);
}
