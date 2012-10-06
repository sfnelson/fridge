package memphis.fridge.client.ioc;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.inject.client.AbstractGinModule;

import memphis.fridge.client.utils.CryptUtils;
import memphis.fridge.client.utils.CryptUtilsImpl;
import memphis.fridge.client.utils.Session;
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
		bind(EventBus.class).to(SimpleEventBus.class);
		bind(LoginView.class).to(LoginWidget.class).asEagerSingleton();
		bind(ProductView.class).to(ProductTableWidget.class);
		bind(CryptUtils.class).to(CryptUtilsImpl.class);
		bind(Session.class).asEagerSingleton();
	}
}
