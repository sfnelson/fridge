package memphis.fridge.client.widgets;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
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

	private final PurchaseWidget parent;
	private final Renderer renderer;

	PurchaseCell(PurchaseWidget parent) {
		this(parent, GWT.<Renderer>create(Renderer.class));
	}

	@Inject
	PurchaseCell(PurchaseWidget parent, Renderer renderer) {
		super("mousedown", "mouseup", "click");
		this.parent = parent;
		this.renderer = renderer;
	}

	@Override
	public void render(Context context, PurchaseEntry value, SafeHtmlBuilder sb) {
		renderer.render(sb, value);
	}

	@Override
	public void onBrowserEvent(Context context, Element parent, PurchaseEntry value, NativeEvent event, ValueUpdater<PurchaseEntry> valueUpdater) {
		event.stopPropagation();
		event.preventDefault();
		if ("mousedown".equals(event.getType())) {
			int button = event.getButton();
			if (button == NativeEvent.BUTTON_LEFT) {
				this.parent.presenter.addToOrder(value.getProductCode(), 1);
			} else if (button == NativeEvent.BUTTON_RIGHT) {
				this.parent.presenter.addToOrder(value.getProductCode(), -1);
			}
		}
	}
}