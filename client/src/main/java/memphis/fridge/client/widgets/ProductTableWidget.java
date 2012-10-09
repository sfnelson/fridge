package memphis.fridge.client.widgets;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RenderablePanel;
import com.google.gwt.view.client.SelectionModel;

import com.google.inject.Inject;
import memphis.fridge.client.rpc.Product;
import memphis.fridge.client.views.ProductView;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 7/10/12
 */
public class ProductTableWidget extends Composite implements ProductView {
	public interface Binder extends UiBinder<RenderablePanel, ProductTableWidget> {
	}

	private Presenter presenter;

	@UiField
	CellList<Product> products;

	ProductTableWidget() {
		this(GWT.<Binder>create(Binder.class));
	}

	@Inject
	ProductTableWidget(Binder binder) {
		super();
		initWidget(binder.createAndBindUi(this));
	}

	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	public void setProducts(List<? extends Product> products) {
		this.products.setRowCount(products.size(), true);
		this.products.setRowData(products);
	}

	void onClick(Product product) {
		this.presenter.productSelected(product);
	}

	@UiFactory
	CellList<Product> createList() {
		return new CellList<Product>(new ProductCell(this)) {
			@Override
			protected void renderRowValues(SafeHtmlBuilder sb, List<Product> values, int start, SelectionModel<? super Product> selectionModel) {
				super.renderRowValues(sb, values, start, selectionModel);
			}
		};
	}
}