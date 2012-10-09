package memphis.fridge.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.ui.RootPanel;

import memphis.fridge.client.views.FridgeView;
import memphis.fridge.client.ioc.ClientInjector;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 30/09/12
 */
public class FridgeClient implements EntryPoint {

	private final ClientInjector injector = GWT.create(ClientInjector.class);

	public void onModuleLoad() {
		FridgeView fridge = injector.getFridgeView();
		injector.getUserPanel().setDisplay(fridge.getUserPanel());
		injector.getProductPanel().setDisplay(fridge.getProductsPanel());
		RootPanel.get().add(fridge);
		injector.getHistoryHandler().handleCurrentHistory();
	}
}
