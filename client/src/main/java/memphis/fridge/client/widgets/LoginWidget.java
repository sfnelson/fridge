package memphis.fridge.client.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RenderablePanel;
import com.google.gwt.user.client.ui.TextBox;

import memphis.fridge.client.views.LoginView;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 30/09/12
 */
public class LoginWidget extends Composite implements LoginView {
	interface Binder extends UiBinder<RenderablePanel, LoginWidget> {
	}

	@UiField
	TextBox username;

	@UiField
	TextBox password;

	@UiField
	Label message;

	LoginView.Presenter presenter;

	public LoginWidget() {
		initWidget(GWT.<Binder>create(Binder.class).createAndBindUi(this));
	}

	public void setPresenter(Presenter p) {
		this.presenter = p;
		Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
			public void execute() {
				username.setFocus(true);
			}
		});
	}

	public String getUser() {
		return username.getValue();
	}

	public String getPass() {
		return password.getValue();
	}

	@UiHandler({"username", "password"})
	void submit(KeyPressEvent ev) {
		if (ev.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER) {
			if (username.getValue().length() <= 0) username.setFocus(true);
			else if (password.getValue().length() <= 0) password.setFocus(true);
			else {
				presenter.doLogin();
			}
		}
	}
}