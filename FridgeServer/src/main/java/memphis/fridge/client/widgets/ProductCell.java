package memphis.fridge.client.widgets;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
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

	ProductCell() {
		this(GWT.<Renderer>create(Renderer.class));
	}

	@Inject
	ProductCell(Renderer renderer) {
		this.renderer = renderer;
	}

	@Override
	public void render(Context context, Product value, SafeHtmlBuilder sb) {
		renderer.render(sb, value);
	}
}