package memphis.fridge.client.activities;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.Place;

import javax.inject.Inject;
import javax.inject.Provider;
import memphis.fridge.client.places.PurchasePlace;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 7/10/12
 */
public class UserPanelActivityMapper implements ActivityMapper {

	@Inject
	Provider<LoginActivity> login;

	@Inject
	Provider<PurchaseActivity> purchase;

	public Activity getActivity(Place place) {
		if (place instanceof PurchasePlace) {
			return purchase.get().init(((PurchasePlace) place).getUsername());
		}

		return login.get();
	}
}
