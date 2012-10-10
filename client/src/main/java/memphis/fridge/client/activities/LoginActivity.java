package memphis.fridge.client.activities;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import javax.inject.Inject;
import memphis.fridge.client.places.SessionPlace;
import memphis.fridge.client.rpc.Session;
import memphis.fridge.client.utils.CryptUtils;
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

	@Inject
	PlaceController pc;

	@Inject
	CryptUtils crypt;

	@Inject
	Scheduler delay;

	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		view.setPresenter(this);
		panel.setWidget(view);
	}

	@Override
	public void onStop() {
		view.setPresenter(null);
	}

	public void doLogin() {
		final String username = view.getUser();
		final String password = crypt.md5(view.getPass());
		delay.scheduleDeferred(new Scheduler.ScheduledCommand() {
			public void execute() {
				pc.goTo(new SessionPlace(username, password));
			}
		});
	}
}
