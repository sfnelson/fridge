package memphis.fridge.client.activities;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import javax.inject.Inject;
import memphis.fridge.client.rpc.NonceRequest;
import memphis.fridge.client.utils.Session;
import memphis.fridge.client.views.LoginView;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 30/09/12
 */
public class LoginActivity extends AbstractActivity implements LoginView.Presenter {

	@Inject
	LoginView view;

	@Inject
	Session session;

	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		view.setPresenter(this);
		panel.setWidget(view);
	}

	public void doLogin() {
		String username = view.getUser();
		String password = view.getPass();
		session.login(username, password);

		session.getNonce(new NonceRequest() {
			public void nonceReady(String nonce) {
				Window.alert("login successful");
			}
		});
	}
}
