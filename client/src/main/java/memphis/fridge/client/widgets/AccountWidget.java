package memphis.fridge.client.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;

import javax.inject.Inject;
import memphis.fridge.client.rpc.Account;
import memphis.fridge.client.utils.NumberUtils;
import memphis.fridge.client.views.AccountView;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 10/10/12
 */
public class AccountWidget extends Composite implements AccountView {
	interface Binder extends UiBinder<HTMLPanel, AccountWidget> {
	}

	private Presenter presenter;

	@UiField
	Label username;

	@UiField
	Label full_name;

	@UiField
	Label email;

	@UiField
	Label balance;

	@UiField
	Label type;

	@UiField
	Button logout;

	AccountWidget() {
		this(GWT.<Binder>create(Binder.class));
	}

	@Inject
	AccountWidget(Binder binder) {
		initWidget(binder.createAndBindUi(this));
	}

	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
		if (presenter == null) {
			this.username.setText("");
			this.full_name.setText("");
			this.email.setText("");
			this.balance.setText("");
		}
	}

	public void setUsername(String username) {
		this.username.setText(username);
	}

	public void setDetails(Account account) {
		this.full_name.setText(account.getRealName());
		this.email.setText(account.getEmail());
		this.balance.setText(NumberUtils.printCurrency(account.getBalance()).asString());

		if (account.isAdmin()) {
			type.setText("Admin");
		} else if (account.isGrad()) {
			type.setText("Member");
		} else {
			type.setText("Guest");
		}
	}

	@UiHandler("logout")
	void logoutClicked(ClickEvent ev) {
		presenter.logout();
	}
}