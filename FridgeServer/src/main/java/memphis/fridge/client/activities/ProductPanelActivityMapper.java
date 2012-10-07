package memphis.fridge.client.activities;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.Place;

import javax.inject.Inject;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 7/10/12
 */
public class ProductPanelActivityMapper implements ActivityMapper {

	@Inject
	ShowProductListActivity products;

	public Activity getActivity(Place place) {
		return products;
	}
}
