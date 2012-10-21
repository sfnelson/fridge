package memphis.fridge.client.activities;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import javax.inject.Inject;
import javax.inject.Provider;
import memphis.fridge.client.places.SessionPlace;
import memphis.fridge.client.rpc.Messages;
import memphis.fridge.client.rpc.TopupRequest;
import memphis.fridge.client.views.ErrorView;
import memphis.fridge.client.views.TopupView;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 22/10/12
 */
public class TopupActivity extends AbstractActivity implements TopupView.Presenter {

	@Inject
	Provider<TopupRequest> req;

	@Inject
	TopupView view;

	@Inject
	ErrorView message;

	private SessionActivity parent;
	private SessionPlace details;
	private Messages.Account account;

	public TopupActivity init(SessionActivity parent, SessionPlace details, Messages.Account account) {
		this.parent = parent;
		this.details = details;
		this.account = account;
		return this;
	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		view.setPresenter(this);
		panel.setWidget(view);
	}

	@Override
	public void topup(int cents) {
		req.get().requestTopup(details, cents, new TopupRequest.Handler() {
			@Override
			public void onSuccess(int balance, int cost) {
				parent.update();
				parent.clearChild();
			}

			@Override
			public void onError(String reason) {
				message.showMessage(reason);
			}
		});
	}

	@Override
	public void onStop() {
		parent.clearChild();
	}
}
