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
import memphis.fridge.client.rpc.RequestAccountInfo;
import memphis.fridge.client.rpc.RequestNonce;
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
	Provider<RequestNonce> nonceRequest;

	@Inject
	Provider<RequestAccountInfo> accountRequest;

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

		// request a nonce to verify username and password
		nonceRequest.get().requestNonce(details, new RequestNonce.Handler() {
			public void onNonceReceived(String nonce) {
				log.info("login verified");
				accountRequest.get().requestAccountInfo(details, nonce, new RequestAccountInfo.Handler() {
					public void onAccountReady(Account account) {
						view.setDetails(account);
						fire(eventBus, account);
					}

					public void onError(Throwable exception) {
						log.warning("unable to get account details: " + exception.getMessage());
					}
				});
			}

			public void onError(Throwable exception) {
				log.warning("login verification failed: " + exception.getMessage());
				later.scheduleDeferred(new Scheduler.ScheduledCommand() {
					public void execute() {
						pc.goTo(new LoginPlace(details.getUsername()));
					}
				});
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
