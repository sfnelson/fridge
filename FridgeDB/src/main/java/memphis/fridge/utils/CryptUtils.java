package memphis.fridge.utils;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;
import java.util.logging.Logger;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import memphis.fridge.exceptions.FridgeException;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 30/09/12
 */
public class CryptUtils {

	private static final Logger log = Logger.getLogger(CryptUtils.class.getName());

	public static final String NONCE_CHARS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
	public static final int NONCE_LENGTH = 20;

	private static final Random RAND = new Random();

	public static String generateNonceToken() {
		StringBuilder s = new StringBuilder();
		for (int i = 0; i < NONCE_LENGTH; i++) {
			int pos = Math.abs(RAND.nextInt()) % NONCE_CHARS.length();
			s.append(NONCE_CHARS.charAt(pos));
		}
		return s.toString();
	}

	public static String sign(String password, Object... toSign) {
		StringBuilder sb = new StringBuilder();
		serialize(sb, toSign);
		String message = sb.toString();

		try {
			SecretKeySpec key = new SecretKeySpec(password.getBytes("CP1252"), "HmacMD5");
			Mac mac = Mac.getInstance("HmacMD5");
			mac.init(key);
			byte[] hmacBytes = mac.doFinal(message.getBytes("CP1252"));
			return DatatypeConverter.printHexBinary(hmacBytes).toLowerCase();
		} catch (UnsupportedEncodingException e) {
			log.severe(e.getMessage());
		} catch (NoSuchAlgorithmException e) {
			log.severe(e.getMessage());
		} catch (InvalidKeyException e) {
			log.severe(e.getMessage());
		}

		throw new FridgeException(1, "Unrecoverable signing error.");
	}

	public static String md5(String message) throws Exception {
		MessageDigest md = MessageDigest.getInstance("MD5");
		return DatatypeConverter.printHexBinary(md.digest(message.getBytes("CP1252"))).toLowerCase();
	}

	private static void serialize(StringBuilder sb, Object obj) {
		if (obj instanceof Pair) serialize(sb, (Pair<?, ?>) obj);
		else if (obj instanceof Object[]) serialize(sb, (Object[]) obj);
		else if (obj instanceof Iterable<?>) serialize(sb, (Iterable<?>) obj);
		else sb.append(obj.toString());
	}

	private static void serialize(StringBuilder sb, Pair<?, ?> pair) {
		serialize(sb, pair.getKey());
		sb.append(',');
		serialize(sb, pair.getValue());
	}

	private static void serialize(StringBuilder sb, Object[] args) {
		serialize(sb, Arrays.asList(args));
	}

	private static void serialize(StringBuilder sb, Iterable<?> args) {
		for (Iterator<?> it = args.iterator(); it.hasNext(); ) {
			serialize(sb, it.next());
			if (it.hasNext()) {
				sb.append(',');
			}
		}
	}
}
