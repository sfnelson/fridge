package memphis.fridge.client;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.RootPanel;

import memphis.fridge.client.ioc.ClientInjector;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 30/09/12
 */
public class FridgeClient extends AbstractActivity implements EntryPoint {

	private final ClientInjector injector = GWT.create(ClientInjector.class);

	public void onModuleLoad() {
		this.start(new Root(), injector.getEventBus());
		injector.getProductsList().start(new Root(), injector.getEventBus());
	}

	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		injector.getLoginActivity().start(panel, eventBus);
	}

	private static class Root implements AcceptsOneWidget {
		private IsWidget w;

		public void setWidget(IsWidget w) {
			clear();
			RootPanel.get().add(w);
			this.w = w;
		}

		public void clear() {
			if (this.w != null) RootPanel.get().remove(this.w);
			this.w = null;
		}
	}
}
