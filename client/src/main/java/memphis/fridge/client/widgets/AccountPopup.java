package memphis.fridge.client.widgets;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;

import javax.inject.Inject;
import memphis.fridge.client.rpc.Messages;
import memphis.fridge.client.views.AccountView;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 22/10/12
 */
public class AccountPopup extends Composite {

	public interface Binder extends UiBinder<HTMLPanel, AccountPopup> {
	}

	interface PopupStyle extends CssResource {
		String show();
	}

	private AccountView.Presenter presenter;

	@UiField
	PopupStyle style;

	@UiField
	PopupPanel popup;

	@UiField
	HTMLPanel content;

	@UiField
	Label full_name;

	@UiField
	Label email;

	@UiField
	Button topup;

	@UiField
	Button transfer;

	@UiField
	Button logout;

	@UiField
	CheckBox stayLoggedIn;

	@UiField
	SimplePanel container;

	Animation show = new Animation() {
		@Override
		protected void onUpdate(double progress) {
			int percent = (int) (progress * 100);
			Style style = content.getElement().getStyle();
			style.setMarginTop(percent - 100, Style.Unit.PCT);
		}
	};

	AccountPopup() {
		this(GWT.<Binder>create(Binder.class));
	}

	@Inject
	AccountPopup(Binder binder) {
		initWidget(binder.createAndBindUi(this));
		popup.addCloseHandler(new CloseHandler<PopupPanel>() {
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				removeStyleName(style.show());
			}
		});
	}

	public void setPresenter(AccountView.Presenter presenter) {
		this.presenter = presenter;
		if (presenter == null) {
			this.full_name.setText("");
			this.email.setText("");
		}
	}

	private Element parent;

	public void toggle() {
		if (popup.isShowing()) {
			popup.hide();
		} else {
			popup.show();
			if (parent == null) {
				parent = this.getElement().getParentElement();
				if (parent != null) {
					popup.addAutoHidePartner(this.getElement().getParentElement());
				}
			}
			show.run(100);
		}
	}

	public void setDetails(Messages.Account account) {
		this.full_name.setText(account.getRealName());
		this.email.setText(account.getEmail());
	}

	@UiHandler("stayLoggedIn")
	void stayLoggedInClicked(ClickEvent ev) {
		if (stayLoggedIn.getValue()) {
			presenter.store();
		} else {
			presenter.clear();
		}

		ev.preventDefault();
		ev.stopPropagation();
	}

	@UiHandler({"stayLoggedIn", "transfer", "topup"})
	void preventDefault(MouseUpEvent ev) {
		ev.preventDefault();
		ev.stopPropagation();
	}

	@UiHandler("topup")
	void topup(ClickEvent ev) {
		presenter.topup(container);
	}

	@UiHandler("transfer")
	void transfer(ClickEvent ev) {
		presenter.transfer(container);
	}

	@UiHandler("logout")
	void logoutClicked(ClickEvent ev) {
		popup.hide();
		presenter.logout();
	}

	@UiFactory
	PopupPanel createPopup() {
		PopupPanel panel = new PopupPanel(true, true) {
			@Override
			public void show() {
				super.show();
				AccountPopup.this.addStyleName(style.show());
			}

			@Override
			public void setPopupPosition(int left, int top) {
				// swallow, we're positioned by our parent
			}
		};
		return panel;
	}
}