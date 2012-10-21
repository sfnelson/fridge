package memphis.fridge.client.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RenderablePanel;
import com.google.gwt.user.client.ui.TextBox;

import javax.inject.Inject;
import memphis.fridge.client.views.ErrorView;
import memphis.fridge.client.views.LoginView;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 30/09/12
 */
public class LoginWidget extends Composite implements LoginView {
	public interface Binder extends UiBinder<RenderablePanel, LoginWidget> {
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

	@Inject
	ErrorView message;

	LoginView.Presenter presenter;

	LoginWidget() {
		this(GWT.<Binder>create(Binder.class));
	}

	@Inject
	LoginWidget(Binder binder) {
		initWidget(binder.createAndBindUi(this));
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
		message.clearMessage();
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

	private void submit() {
		if (username.getValue().length() <= 0) {
			username.setFocus(true);
			message.showMessage("Enter your username");
		} else if (password.getValue().length() <= 0) {
			password.setFocus(true);
			message.showMessage("Enter your password");
		} else {
			presenter.doLogin();
		}
	}
}