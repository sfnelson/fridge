package memphis.fridge.client.activities;

import java.util.logging.Logger;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import javax.inject.Inject;
import javax.inject.Provider;
import memphis.fridge.client.places.LoginPlace;
import memphis.fridge.client.places.SessionPlace;
import memphis.fridge.client.rpc.Account;
import memphis.fridge.client.rpc.AccountRequest;
import memphis.fridge.client.views.AccountView;

import static memphis.fridge.client.events.AccountEvent.fire;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 10/10/12
 */
public class SessionActivity extends AbstractActivity implements AccountView.Presenter {

	private static final Logger log = Logger.getLogger("session");

	private SessionPlace details;

	@Inject
	Provider<AccountRequest> accountRequest;

	@Inject
	PlaceController pc;

	@Inject
	AccountView view;

	@Inject
	Scheduler later;

	public SessionActivity init(SessionPlace place) {
		this.details = place;

		return this;
	}

	public void start(AcceptsOneWidget panel, final EventBus eventBus) {
		if (details.getSecret() == null) {
			logout();
			return;
		}

		view.setPresenter(this);
		view.setUsername(details.getUsername());
		panel.setWidget(view);

		// request account details to verify username and password
		accountRequest.get().requestAccountInfo(details, new AccountRequest.Handler() {
			public void onAccountReady(Account account) {
				view.setDetails(account);
				fire(eventBus, account);
			}

			public void onError(Throwable exception) {
				log.warning("unable to get account details: " + exception.getMessage());
				logout();
			}
		});
	}

	public void logout() {
		later.scheduleDeferred(new Scheduler.ScheduledCommand() {
			public void execute() {
				pc.goTo(LoginPlace.LOGIN);
			}
		});
	}
}
