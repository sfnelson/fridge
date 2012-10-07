package memphis.fridge.client.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RenderablePanel;
import com.google.gwt.user.client.ui.TextBox;

import javax.inject.Inject;
import memphis.fridge.client.rpc.Product;
import memphis.fridge.client.views.PurchaseView;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 7/10/12
 */
public class PurchaseWidget extends Composite implements PurchaseView {
	public interface Binder extends UiBinder<RenderablePanel, PurchaseWidget> {
	}

	private Presenter presenter;

	@UiField
	TextBox product;

	@UiField
	TextBox amount;

	PurchaseWidget() {
		this(GWT.<Binder>create(Binder.class));
	}

	@Inject
	PurchaseWidget(Binder binder) {
		initWidget(binder.createAndBindUi(this));
	}

	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	public void addProduct(Product product, int count) {
	}

	@UiHandler({"product", "amount", "add"})
	void keyPress(KeyPressEvent ev) {
		if (ev.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER) {
			String code = product.getValue();
			String num = amount.getValue();
			presenter.addToOrder(code, num);
		}
	}
}
