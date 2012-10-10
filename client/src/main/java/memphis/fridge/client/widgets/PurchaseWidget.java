package memphis.fridge.client.widgets;

import java.util.Collections;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RenderablePanel;
import com.google.gwt.user.client.ui.TextBox;

import javax.inject.Inject;
import memphis.fridge.client.rpc.PurchaseEntry;
import memphis.fridge.client.utils.NumberUtils;
import memphis.fridge.client.views.PurchaseView;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 7/10/12
 */
public class PurchaseWidget extends Composite implements PurchaseView {
	public interface Binder extends UiBinder<RenderablePanel, PurchaseWidget> {
	}

	interface Style extends CssResource {
		String purchase();

		String hasFocus();

		String info();

		String amount();

		String balance();

		String total();

		String cart();
	}

	Presenter presenter;

	@UiField
	Style style;

	@UiField
	TextBox product;

	@UiField
	TextBox amount;

	@UiField
	CellList<PurchaseEntry> cart;

	//@UiField
	Label name;

	//@UiField
	Label type;

	@UiField
	TextBox balance;

	@UiField
	TextBox total;

	PurchaseWidget() {
		this(GWT.<Binder>create(Binder.class));
	}

	@Inject
	PurchaseWidget(Binder binder) {
		initWidget(binder.createAndBindUi(this));
	}

	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
		Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
			public void execute() {
				//name.setText("");
				//type.setText("");
				balance.setValue("");
				clearOrder();
			}
		});
	}

	public void clearOrder() {
		cart.setRowCount(0);
		cart.setRowData(Collections.<PurchaseEntry>emptyList());
		total.setValue("");
		product.setValue("");
		amount.setValue("1");
		product.setFocus(true);
	}

	public void setCartContents(List<PurchaseEntry> content) {
		if (content == null) {
			cart.setRowCount(0);
			cart.setRowData(Collections.<PurchaseEntry>emptyList());
		} else {
			cart.setRowCount(content.size());
			cart.setRowData(content);
		}
	}

	public void setOrderDetails(int cost) {
		total.setText(NumberUtils.printCurrency(cost).asString());
	}

	public void setProduct(final String productCode) {
		Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
			public void execute() {
				product.setValue("");
				product.setFocus(true);
				product.selectAll();
			}
		});
	}

	public void setBalance(int balance) {
		this.balance.setValue(NumberUtils.printCurrency(balance).asString());
	}

	@UiHandler({"product", "amount", "add"})
	void keyPress(KeyPressEvent ev) {
		if (ev.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER) {
			try {
				if (product.getValue().length() == 0) presenter.submitOrder();
				else if (amount.getValue().length() == 0) amount.setFocus(true);
				else {
					String code = product.getValue();
					int num = Integer.valueOf(amount.getValue());
					presenter.addToOrder(code, num);
					product.setValue("");
					amount.setValue("1");
					product.setFocus(true);
				}
			} catch (NumberFormatException ex) {
				amount.selectAll();
				amount.setFocus(true);
			}
		}
	}

	@UiHandler({"product", "amount"})
	void focus(FocusEvent ev) {
		ev.getRelativeElement().getParentElement().setClassName(style.hasFocus());
	}

	@UiHandler({"product", "amount"})
	void blur(BlurEvent ev) {
		ev.getRelativeElement().getParentElement().setClassName("");
	}

	@UiHandler({"buy"})
	void purchase(ClickEvent ev) {
		presenter.submitOrder();
	}

	@UiFactory
	CellList<PurchaseEntry> createCart() {
		return new CellList<PurchaseEntry>(new PurchaseCell(this));
	}
}
