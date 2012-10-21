package memphis.fridge.client.ioc;

import com.google.gwt.activity.shared.ActivityManager;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.inject.client.GinModules;
import com.google.gwt.inject.client.Ginjector;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryHandler;

import javax.inject.Named;
import memphis.fridge.client.views.FridgeView;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 30/09/12
 */
@GinModules(ClientModule.class)
public interface ClientInjector extends Ginjector {
	EventBus getEventBus();

	PlaceHistoryHandler getHistoryHandler();

	FridgeView getFridgeView();

	PlaceController getPlaceController();

	@Named("session-panel")
	ActivityManager getSessionPanel();

	@Named("user-panel")
	ActivityManager getUserPanel();

	@Named("product-panel")
	ActivityManager getProductPanel();
}
