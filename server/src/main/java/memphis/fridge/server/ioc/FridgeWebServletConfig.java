package memphis.fridge.server.ioc;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.matcher.Matchers;
import com.google.inject.persist.PersistFilter;
import com.google.inject.persist.jpa.JpaPersistModule;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.protobuf.ExtensionRegistry;
import com.sun.jersey.guice.JerseyServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;
import memphis.fridge.server.AccountRequest;
import memphis.fridge.server.OrderRequest;
import memphis.fridge.server.ProductImages;
import memphis.fridge.server.ProductsRequest;
import memphis.fridge.server.io.SignedJsonMessageReader;
import memphis.fridge.server.io.SignedJsonMessageWriter;

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

				bindInterceptor(Matchers.any(), Matchers.annotatedWith(RequireAuthenticated.class),
						new AuthenticationChecker.CheckIsAuthenticated(getProvider(SessionState.class)));
				bindInterceptor(Matchers.any(), Matchers.annotatedWith(RequireAdmin.class),
						new AuthenticationChecker.CheckIsAdmin(getProvider(SessionState.class)));

				bind(SignedJsonMessageReader.class);
				bind(SignedJsonMessageWriter.class);

				bind(AccountRequest.class);
				bind(ProductsRequest.class);
				bind(OrderRequest.class);
				bind(ProductImages.class);

				filter("/memphis/fridge/rest/*").through(PersistFilter.class);
				serve("/memphis/fridge/rest/*").with(GuiceContainer.class);
			}

			@Provides
			ExtensionRegistry getExtensionRegistry() {
				return ExtensionRegistry.getEmptyRegistry();
			}
		});
	}
}
