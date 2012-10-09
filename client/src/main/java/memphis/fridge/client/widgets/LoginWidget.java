package memphis.fridge.client.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;

import memphis.fridge.client.views.LoginView;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 30/09/12
 */
public class LoginWidget extends Composite implements LoginView {
	interface Binder extends UiBinder<RenderablePanel, LoginWidget> {
	}

	interface Style extends CssResource {
		String login();

		String label();

		String hasFocus();

		String message();

		String show();
	}

	@UiField
	Style style;

	@UiField
	TextBox username;

	@UiField
	TextBox password;

	@UiField
	Button login;

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
				username.setValue("");
				password.setValue("");
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
	void focus(FocusEvent ev) {
		ev.getRelativeElement().getParentElement().setClassName(style.hasFocus());
	}

	@UiHandler({"username", "password"})
	void blur(BlurEvent ev) {
		ev.getRelativeElement().getParentElement().setClassName("");
		clearMessage();
	}

	@UiHandler({"username", "password", "login"})
	void submit(KeyPressEvent ev) {
		if (ev.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER) {
			submit();
		}
	}

	@UiHandler({"login"})
	void loginClicked(ClickEvent ev) {
		if (NativeEvent.BUTTON_LEFT == ev.getNativeButton()) {
			submit();
		}
	}

	@UiHandler({"message"})
	void messageClicked(ClickEvent ev) {
		clearMessage();
	}

	private void showMessage(String message) {
		this.message.setText(message);
		this.message.setStyleName(style.show());
		Scheduler.get().scheduleFixedDelay(new Scheduler.RepeatingCommand() {
			public boolean execute() {
				clearMessage();
				return false;
			}
		}, 2000);
	}

	private void clearMessage() {
		this.message.setStyleName("");
	}

	private void submit() {
		if (username.getValue().length() <= 0) {
			username.setFocus(true);
			showMessage("Enter your username");
		} else if (password.getValue().length() <= 0) {
			password.setFocus(true);
			showMessage("Enter your password");
		} else {
			presenter.doLogin();
		}
	}
}