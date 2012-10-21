package memphis.fridge.client.activities;

import java.util.logging.Logger;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.storage.client.Storage;
import com.google.gwt.storage.client.StorageMap;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import javax.inject.Inject;
import javax.inject.Provider;
import memphis.fridge.client.places.LoginPlace;
import memphis.fridge.client.places.SessionPlace;
import memphis.fridge.client.rpc.AccountRequest;
import memphis.fridge.client.rpc.Messages;
import memphis.fridge.client.views.AccountView;
import memphis.fridge.client.views.ErrorView;

import static memphis.fridge.client.events.AccountEvent.fire;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 10/10/12
 */
public class SessionActivity extends AbstractActivity implements AccountView.Presenter {

	private static final Logger log = Logger.getLogger("session");

	private SessionPlace details;

	private Messages.Account account;

	private EventBus eventBus;

	@Inject
	Provider<AccountRequest> accountRequest;

	@Inject
	Provider<TopupActivity> topup;

	@Inject
	Provider<TransferActivity> transfer;

	@Inject
	PlaceController pc;

	@Inject
	AccountView view;

	@Inject
	Scheduler later;

	@Inject
	ErrorView message;

	public SessionActivity init(SessionPlace place) {
		this.details = place;

		return this;
	}

	public void start(AcceptsOneWidget panel, final EventBus eventBus) {
		this.eventBus = eventBus;

		if (details.getSecret() == null) {
			final Place target = load();
			later.scheduleDeferred(new Scheduler.ScheduledCommand() {
				public void execute() {
					pc.goTo(target);
				}
			});
			return;
		}

		view.setPresenter(this);
		view.setUsername(details.getUsername());
		panel.setWidget(view);

		if (load() instanceof SessionPlace) {
			setStoreDetails(true);
		}

		update();
	}

	public void update() {
		// request account details to verify username and password
		accountRequest.get().requestAccountInfo(details, new AccountRequest.Handler() {
			public void onAccountReady(Messages.Account account) {
				SessionActivity.this.account = account;
				view.setDetails(account);
				fire(eventBus, account);
			}

			public void onError(String message) {
				SessionActivity.this.message.showMessage(message);
				logout();
			}
		});
	}

	@Override
	public void topup(AcceptsOneWidget container) {
		topup.get().init(this, details, account).start(container, eventBus);
	}

	@Override
	public void transfer(AcceptsOneWidget container) {
		transfer.get().init(this, details).start(container, eventBus);
	}

	public static final String SESSION_USER = "username";
	public static final String SESSION_SECRET = "secret";

	@Override
	public void store() {
		Storage loginStore = Storage.getLocalStorageIfSupported();
		if (loginStore != null) {
			log.info("storing session details");
			StorageMap map = new StorageMap(loginStore);
			map.put(SESSION_USER, details.getUsername());
			map.put(SESSION_SECRET, details.getSecret());
			setStoreDetails(true);
		} else {
			setStoreDetails(false);
		}
	}

	@Override
	public void clear() {
		Storage loginStore = Storage.getLocalStorageIfSupported();
		if (loginStore != null) {
			log.info("clearing local storage");
			StorageMap map = new StorageMap(loginStore);
			map.remove(SESSION_USER);
			map.remove(SESSION_SECRET);
		}
		setStoreDetails(false);
	}

	@Override
	public void logout() {
		clear();
		later.scheduleDeferred(new Scheduler.ScheduledCommand() {
			public void execute() {
				pc.goTo(LoginPlace.LOGIN);
			}
		});
	}

	public static Place load() {
		Storage loginStore = Storage.getLocalStorageIfSupported();
		if (loginStore != null) {
			StorageMap data = new StorageMap(loginStore);
			if (data.containsKey(SessionActivity.SESSION_USER) && data.containsKey(SessionActivity.SESSION_SECRET)) {
				String name = data.get(SessionActivity.SESSION_USER);
				String secret = data.get(SessionActivity.SESSION_SECRET);
				return new SessionPlace(name, secret);
			}
		}
		return LoginPlace.LOGIN;
	}

	private void setStoreDetails(final boolean storeDetails) {
		later.scheduleDeferred(new Scheduler.ScheduledCommand() {
			@Override
			public void execute() {
				view.setStoreDetails(storeDetails);
			}
		});
	}

	public void clearChild() {
		view.clearChildren();
	}
}
