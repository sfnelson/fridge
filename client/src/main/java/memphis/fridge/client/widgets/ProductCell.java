package memphis.fridge.client.widgets;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiRenderer;

import com.google.inject.Inject;
import memphis.fridge.client.rpc.Product;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 7/10/12
 */
public class ProductCell extends AbstractCell<Product> {
	interface Renderer extends UiRenderer {
		void render(SafeHtmlBuilder b, Product value);
	}

	private final Renderer renderer;
	private final ProductTableWidget parent;

	ProductCell(ProductTableWidget parent) {
		this(GWT.<Renderer>create(Renderer.class), parent);
	}

	@Inject
	ProductCell(Renderer renderer, ProductTableWidget parent) {
		super("click");
		this.parent = parent;
		this.renderer = renderer;
	}

	@Override
	public void render(Context context, Product value, SafeHtmlBuilder sb) {
		renderer.render(sb, value);
	}

	@Override
	public void onBrowserEvent(Context context, Element parent, Product product, NativeEvent event, ValueUpdater<Product> valueUpdater) {
		if ("click".equals(event.getType())) {
			this.parent.onClick(product);
		}
	}
}