package memphis.fridge.client.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.SimplePanel;

import javax.inject.Inject;
import memphis.fridge.client.views.ErrorView;
import memphis.fridge.client.views.FridgeView;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 8/10/12
 */
public class FridgeWidget extends Composite implements FridgeView {
	public interface Binder extends UiBinder<HTMLPanel, FridgeWidget> {
	}

	@UiField
	SimplePanel account;

	@UiField
	SimplePanel user;

	@UiField
	SimplePanel products;

	@UiField
	ErrorWidget messages;

	FridgeWidget() {
		this(GWT.<Binder>create(Binder.class));
	}

	@Inject
	FridgeWidget(Binder binder) {
		initWidget(binder.createAndBindUi(this));
	}

	public AcceptsOneWidget getAccountPanel() {
		return account;
	}

	public AcceptsOneWidget getUserPanel() {
		return user;
	}

	public AcceptsOneWidget getProductsPanel() {
		return products;
	}

	public ErrorView getErrorWidget() {
		return messages;
	}
}
