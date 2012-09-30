package memphis.fridge.client.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.TextBox;

import memphis.fridge.client.views.LoginView;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 30/09/12
 */
public class LoginWidget extends Composite implements LoginView {
	interface Binder extends UiBinder<HTMLPanel, LoginWidget> {
	}

	@UiField
	TextBox username;

	@UiField
	TextBox password;

	LoginView.Presenter presenter;

	public LoginWidget() {
		initWidget(GWT.<Binder>create(Binder.class).createAndBindUi(this));
	}

	public void setPresenter(Presenter p) {
		this.presenter = p;
	}

	public String getUser() {
		return username.getValue();
	}

	public String getPass() {
		return password.getValue();
	}

	@UiHandler("login")
	void login(ClickEvent ev) {
		presenter.doLogin();
	}
}