package memphis.fridge.client.ioc;

import com.google.gwt.activity.shared.ActivityManager;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.inject.client.GinModules;
import com.google.gwt.inject.client.Ginjector;
import com.google.gwt.place.shared.PlaceHistoryHandler;

import javax.inject.Named;
import memphis.fridge.client.activities.ShowProductListActivity;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 30/09/12
 */
@GinModules(ClientModule.class)
public interface ClientInjector extends Ginjector {
	EventBus getEventBus();

	PlaceHistoryHandler getHistoryHandler();

	@Named("user-panel")
	ActivityManager getUserPanelManager();

	@Named("product-panel")
	ActivityManager getProductPanelManager();

	ShowProductListActivity getProductsList();
}
