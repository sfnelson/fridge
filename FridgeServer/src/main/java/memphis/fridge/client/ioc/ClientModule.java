package memphis.fridge.client.ioc;

import com.google.gwt.activity.shared.ActivityManager;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.inject.client.AbstractGinModule;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryHandler;

import com.google.inject.Provides;
import javax.inject.Named;
import javax.inject.Singleton;
import memphis.fridge.client.activities.ProductPanelActivityMapper;
import memphis.fridge.client.activities.UserPanelActivityMapper;
import memphis.fridge.client.places.FridgePlaceMapper;
import memphis.fridge.client.places.LoginPlace;
import memphis.fridge.client.rpc.Session;
import memphis.fridge.client.utils.CryptUtils;
import memphis.fridge.client.utils.CryptUtilsImpl;
import memphis.fridge.client.views.LoginView;
import memphis.fridge.client.views.ProductView;
import memphis.fridge.client.widgets.LoginWidget;
import memphis.fridge.client.widgets.ProductTableWidget;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 30/09/12
 */
public class ClientModule extends AbstractGinModule {
	@Override
	protected void configure() {
		bind(EventBus.class).to(SimpleEventBus.class).asEagerSingleton();
		bind(LoginView.class).to(LoginWidget.class).asEagerSingleton();
		bind(ProductView.class).to(ProductTableWidget.class);
		bind(CryptUtils.class).to(CryptUtilsImpl.class);
		bind(Session.class).asEagerSingleton();
	}

	@Provides
	@SuppressWarnings("deprecated")
	PlaceHistoryHandler historyHandler(PlaceController pc, EventBus eb) {
		FridgePlaceMapper pm = GWT.create(FridgePlaceMapper.class);
		PlaceHistoryHandler hh = new PlaceHistoryHandler(pm);
		hh.register(pc, eb, LoginPlace.LOGIN);
		return hh;
	}

	@Provides
	@Singleton
	@SuppressWarnings("deprecated")
	PlaceController placeController(EventBus eb) {
		return new PlaceController(eb);
	}

	@Provides
	@Singleton
	@Named("user-panel")
	ActivityManager userManager(EventBus eb, UserPanelActivityMapper mapper) {
		return new ActivityManager(mapper, eb);
	}

	@Provides
	@Singleton
	@Named("product-panel")
	ActivityManager productManager(EventBus eb, ProductPanelActivityMapper mapper) {
		return new ActivityManager(mapper, eb);
	}

	@Provides
	@Singleton
	Scheduler getScheduler() {
		return Scheduler.get();
	}
}
