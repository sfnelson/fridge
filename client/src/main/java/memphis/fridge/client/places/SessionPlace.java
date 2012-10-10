package memphis.fridge.client.places;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 7/10/12
 */
public class SessionPlace extends Place {

	private final String name;
	private final String password_md5;

	public SessionPlace(String name, String password_md5) {
		this.name = name;
		this.password_md5 = password_md5;
	}

	public String getUsername() {
		return name;
	}

	public String getSecret() {
		return password_md5;
	}

	@Prefix("account")
	public static class Tokenizer implements PlaceTokenizer<SessionPlace> {
		public SessionPlace getPlace(String name) {
			return new SessionPlace(name, null);
		}

		public String getToken(SessionPlace place) {
			return place.name;
		}
	}
}
