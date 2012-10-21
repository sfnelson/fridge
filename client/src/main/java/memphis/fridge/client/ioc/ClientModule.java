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
import memphis.fridge.client.activities.SessionActivityMapper;
import memphis.fridge.client.activities.UserPanelActivityMapper;
import memphis.fridge.client.places.FridgePlaceMapper;
import memphis.fridge.client.places.LoginPlace;
import memphis.fridge.client.utils.CryptUtils;
import memphis.fridge.client.utils.CryptUtilsImpl;
import memphis.fridge.client.views.*;
import memphis.fridge.client.widgets.*;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 30/09/12
 */
public class ClientModule extends AbstractGinModule {
	@Override
	protected void configure() {
		bind(AccountView.class).to(AccountWidget.class).asEagerSingleton();
		bind(EventBus.class).to(SimpleEventBus.class).asEagerSingleton();
		bind(LoginView.class).to(LoginWidget.class).asEagerSingleton();
		bind(ProductView.class).to(ProductTableWidget.class).asEagerSingleton();
		bind(PurchaseView.class).to(PurchaseWidget.class).asEagerSingleton();
		bind(TopupView.class).to(TopupWidget.class);
		bind(TransferView.class).to(TransferWidget.class);
		bind(CryptUtils.class).to(CryptUtilsImpl.class);
		bind(FridgeView.class).to(FridgeWidget.class).asEagerSingleton();
	}

	@Provides
	@SuppressWarnings({"deprecation"})
	PlaceHistoryHandler historyHandler(PlaceController pc, EventBus eb) {
		FridgePlaceMapper pm = GWT.create(FridgePlaceMapper.class);
		PlaceHistoryHandler hh = new PlaceHistoryHandler(pm);
		hh.register(pc, eb, LoginPlace.LOGIN);
		return hh;
	}

	@Provides
	@Singleton
	@SuppressWarnings({"deprecation"})
	PlaceController placeController(EventBus eb) {
		return new PlaceController(eb);
	}

	@Provides
	@Singleton
	@Named("session-panel")
	ActivityManager sessionManager(EventBus eb, SessionActivityMapper mapper) {
		return new ActivityManager(mapper, eb);
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
	ErrorView errorView(FridgeView fridge) {
		return fridge.getErrorWidget();
	}

	@Provides
	@Singleton
	Scheduler getScheduler() {
		return Scheduler.get();
	}
}
