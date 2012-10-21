package memphis.fridge.client.widgets;

import java.util.logging.Logger;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.TextBox;

import javax.inject.Inject;
import memphis.fridge.client.views.TopupView;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 22/10/12
 */
public class TopupWidget extends Composite implements TopupView {
	public interface Binder extends UiBinder<HTMLPanel, TopupWidget> {
	}

	private static final Logger log = Logger.getLogger(TopupWidget.class.getName());

	private Presenter presenter;

	@UiField
	TextBox amount;

	TopupWidget() {
		this(GWT.<Binder>create(Binder.class));
	}

	@Inject
	TopupWidget(Binder binder) {
		initWidget(binder.createAndBindUi(this));
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	@UiHandler("topup")
	void submit(ClickEvent ev) {
		try {
			double value = NumberFormat.getDecimalFormat().parse(amount.getValue());
			int intValue = ((int) value) * 100 + ((int) (value * 100) % 100);
			presenter.topup(intValue);
		} catch (NumberFormatException ex) {
			log.warning("error parsing topup input: " + ex.getMessage());
		}
	}
}