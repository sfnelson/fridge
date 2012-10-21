package memphis.fridge.client.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;

import javax.inject.Inject;
import memphis.fridge.client.rpc.Messages;
import memphis.fridge.client.utils.NumberUtils;
import memphis.fridge.client.views.AccountView;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 10/10/12
 */
public class AccountWidget extends Composite implements AccountView {
	public interface Binder extends UiBinder<HTMLPanel, AccountWidget> {
	}

	interface Style extends CssResource {
		String loginPanel();

		String balance();

		String type();
	}

	private Presenter presenter;

	@UiField
	Style style;

	@UiField
	Label username;

	@UiField
	Label balance;

	@UiField
	Label type;

	@UiField
	AccountPopup popup;

	AccountWidget() {
		this(GWT.<Binder>create(Binder.class));
	}

	@Inject
	AccountWidget(Binder binder) {
		sinkEvents(Event.getTypeInt(BrowserEvents.CLICK));
		initWidget(binder.createAndBindUi(this));
		addHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				popup.setPresenter(presenter);
				popup.toggle();
			}
		}, ClickEvent.getType());
	}

	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
		if (presenter == null) {
			this.username.setText("");
			this.balance.setText("");
		}
		this.popup.setPresenter(presenter);
	}

	public void setUsername(String username) {
		this.username.setText(username);
	}

	@Override
	public void setStoreDetails(boolean storeDetails) {
		popup.stayLoggedIn.setValue(storeDetails);
	}

	@Override
	public void clearChildren() {
		popup.container.clear();
	}

	public void setDetails(Messages.Account account) {
		this.balance.setText(NumberUtils.printCurrency(account.getBalance()).asString());

		if (account.isAdmin()) {
			type.setText("admin");
		} else if (account.isGrad()) {
			type.setText("graduate");
		} else {
			type.setText("undergrad");
		}

		popup.setDetails(account);
	}
}