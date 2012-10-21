package memphis.fridge.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.RootPanel;

import memphis.fridge.client.activities.SessionActivity;
import memphis.fridge.client.ioc.ClientInjector;
import memphis.fridge.client.views.FridgeView;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 30/09/12
 */
public class FridgeClient implements EntryPoint {

	private final ClientInjector injector = GWT.create(ClientInjector.class);

	public void onModuleLoad() {
		FridgeView fridge = injector.getFridgeView();
		injector.getSessionPanel().setDisplay(fridge.getAccountPanel());
		injector.getUserPanel().setDisplay(fridge.getUserPanel());
		injector.getProductPanel().setDisplay(fridge.getProductsPanel());
		RootPanel.get().add(fridge);
		injector.getHistoryHandler().handleCurrentHistory();

		Place place = SessionActivity.load();
		injector.getPlaceController().goTo(place);
	}
}
