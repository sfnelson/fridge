package memphis.fridge.client.places;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 7/10/12
 */
public class PurchasePlace extends Place {

	private final String name;

	public PurchasePlace(String name) {
		this.name = name;
	}

	@Prefix("account")
	public static class Tokenizer implements PlaceTokenizer<PurchasePlace> {
		public PurchasePlace getPlace(String name) {
			return new PurchasePlace(name);
		}

		public String getToken(PurchasePlace place) {
			return place.name;
		}
	}
}
