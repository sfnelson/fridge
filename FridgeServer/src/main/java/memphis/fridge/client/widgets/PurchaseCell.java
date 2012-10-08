package memphis.fridge.client.widgets;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiRenderer;

import com.google.inject.Inject;
import memphis.fridge.client.rpc.PurchaseEntry;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 8/10/12
 */
public class PurchaseCell extends AbstractCell<PurchaseEntry> {
	interface Renderer extends UiRenderer {
		void render(SafeHtmlBuilder b, PurchaseEntry value);
	}

	private final Renderer renderer;

	PurchaseCell() {
		this(GWT.<Renderer>create(Renderer.class));
	}

	@Inject
	PurchaseCell(Renderer renderer) {
		this.renderer = renderer;
	}

	@Override
	public void render(Context context, PurchaseEntry value, SafeHtmlBuilder sb) {
		renderer.render(sb, value);
	}
}