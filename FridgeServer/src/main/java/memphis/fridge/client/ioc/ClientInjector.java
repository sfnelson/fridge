package memphis.fridge.client.ioc;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.inject.client.GinModules;
import com.google.gwt.inject.client.Ginjector;

import memphis.fridge.client.activities.LoginActivity;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 30/09/12
 */
@GinModules(ClientModule.class)
public interface ClientInjector extends Ginjector {
	EventBus getEventBus();

	LoginActivity getLoginActivity();
}
