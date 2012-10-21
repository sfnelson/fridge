package memphis.fridge.client.activities;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import javax.inject.Inject;
import javax.inject.Provider;
import memphis.fridge.client.places.SessionPlace;
import memphis.fridge.client.rpc.TransferRequest;
import memphis.fridge.client.views.ErrorView;
import memphis.fridge.client.views.TransferView;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 22/10/12
 */
public class TransferActivity extends AbstractActivity implements TransferView.Presenter {

	@Inject
	Provider<TransferRequest> req;

	@Inject
	TransferView view;

	@Inject
	ErrorView message;

	private SessionActivity parent;
	private SessionPlace details;

	public TransferActivity init(SessionActivity parent, SessionPlace details) {
		this.parent = parent;
		this.details = details;
		return this;
	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		view.setPresenter(this);
		panel.setWidget(view);
	}

	@Override
	public void onStop() {
		parent.clearChild();
	}

	@Override
	public void transfer(String toUsername, int cents) {
		req.get().requestTransfer(details, details.getUsername(), toUsername, cents, new TransferRequest.Handler() {
			@Override
			public void onSuccess(int balance, int cost) {
				parent.update();
				parent.clearChild();
			}

			@Override
			public void onError(String reason) {
				message.showMessage("Transfer failed: " + reason);
			}
		});
	}
}
