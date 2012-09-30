package memphis.fridge.client.utils;

import java.util.Random;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 30/09/12
 */
public class CryptUtilsImpl implements CryptUtils {

	public static final String NONCE_CHARS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
	public static final int NONCE_LENGTH = 20;

	private static final Random RAND = new Random();

	public String generateNonceToken() {
		StringBuilder s = new StringBuilder();
		for (int i = 0; i < NONCE_LENGTH; i++) {
			int pos = Math.abs(RAND.nextInt()) % NONCE_CHARS.length();
			s.append(NONCE_CHARS.charAt(pos));
		}
		return s.toString();
	}

	public String sign(String password, Object... toSign) {
		StringBuilder message = new StringBuilder();
		for (int i = 0; i < toSign.length; i++) {
			if (i > 0) message.append(',');
			message.append(toSign[i]);
		}
		return _sign(password, message.toString());
	}

	private native String _sign(String password, String message) /*-{
		return $wnd.CryptoJS.HmacMD5(message, password);
	}-*/;

	public String md5(String message) {
		return _md5(message);
	}

	private native String _md5(String message) /*-{
		return $wnd.CryptoJS.MD5(message);
	}-*/;
}
