package memphis.fridge.client.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;

import javax.inject.Inject;
import memphis.fridge.client.views.ErrorView;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 22/10/12
 */
public class ErrorWidget extends Composite implements ErrorView {
	public interface Binder extends UiBinder<HTMLPanel, ErrorWidget> {
	}

	interface Style extends CssResource {
		String message();

		String show();
	}

	@UiField
	Style style;

	@UiField
	Label message;

	ErrorWidget() {
		this(GWT.<Binder>create(Binder.class));
	}

	@Inject
	ErrorWidget(Binder binder) {
		initWidget(binder.createAndBindUi(this));
	}

	@Override
	public void showMessage(String message) {
		this.message.setText(message);
		this.message.setStyleName(style.show());
		Scheduler.get().scheduleFixedDelay(new Scheduler.RepeatingCommand() {
			public boolean execute() {
				clearMessage();
				return false;
			}
		}, 15000);
	}

	public void clearMessage() {
		this.message.setStyleName("");
	}

	@UiHandler({"message"})
	void messageClicked(ClickEvent ev) {
		clearMessage();
	}
}