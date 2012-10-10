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
 * Date: 10/10/12
 */
public class SessionActivityMapper implements ActivityMapper {

	@Inject
	PlaceController pc;

	@Inject
	Provider<SessionActivity> session;

	public Activity getActivity(Place place) {
		if (place instanceof SessionPlace) {
			SessionPlace details = (SessionPlace) place;
			return session.get().init(details);
		}

		return null;
	}
}
