package memphis.fridge.client.events;

import com.google.web.bindery.event.shared.Event;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;

import memphis.fridge.client.rpc.Product;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 10/10/12
 */
public class ProductEvent extends Event<ProductHandler> {

	public static final Type<ProductHandler> TYPE = new Type<ProductHandler>();

	public static HandlerRegistration register(EventBus eb, ProductHandler handler) {
		return eb.addHandler(TYPE, handler);
	}

	private final Product p;

	public ProductEvent(Product p) {
		this.p = p;
	}

	@Override
	public Type getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(ProductHandler handler) {
		handler.productSelected(p);
	}
}
