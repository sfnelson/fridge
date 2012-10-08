package memphis.fridge.server.ioc;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.persist.PersistFilter;
import com.google.inject.persist.jpa.JpaPersistModule;
import com.google.inject.servlet.GuiceServletContextListener;
import com.sun.jersey.guice.JerseyServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;
import memphis.fridge.server.GenerateNonceRequest;
import memphis.fridge.server.GetProductsRequest;
import memphis.fridge.server.OrderRequest;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 28/09/12
 */
public class FridgeWebServletConfig extends GuiceServletContextListener {
	@Override
	protected Injector getInjector() {
		return Guice.createInjector(new JerseyServletModule() {
			@Override
			protected void configureServlets() {
				install(new JpaPersistModule("FridgeDB"));

				bind(GenerateNonceRequest.class);
				bind(GetProductsRequest.class);
				bind(OrderRequest.class);

				filter("/fridge/rest/*").through(PersistFilter.class);
				serve("/fridge/rest/*").with(GuiceContainer.class);
			}
		});
	}
}
