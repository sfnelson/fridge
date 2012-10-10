package memphis.fridge.client.places;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 7/10/12
 */
public class LoginPlace extends Place {

	public static final LoginPlace LOGIN = new LoginPlace(null);

	private final String username;

	public LoginPlace(String username) {
		this.username = username;
	}

	public String getUsername() {
		return username;
	}

	@Prefix("login")
	public static class Tokenizer implements PlaceTokenizer<LoginPlace> {
		public LoginPlace getPlace(String token) {
			return LOGIN;
		}

		public String getToken(LoginPlace place) {
			return null;
		}
	}
}
