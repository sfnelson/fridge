package memphis.fridge.client.activities;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;

import javax.inject.Inject;
import javax.inject.Provider;
import memphis.fridge.client.places.SessionPlace;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 7/10/12
 */
public class UserPanelActivityMapper implements ActivityMapper {

	@Inject
	Provider<LoginActivity> login;

	@Inject
	Provider<PurchaseActivity> purchase;

	@Inject
	PlaceController pc;

	public Activity getActivity(Place place) {
		if (place instanceof SessionPlace) {
			return purchase.get().init((SessionPlace) place);
		}

		return login.get();
	}
}
