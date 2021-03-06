package memphis.fridge.server.ioc;

import com.google.inject.*;
import com.google.inject.matcher.Matchers;
import com.google.inject.servlet.RequestScoped;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 12/10/12
 */
public class AuthModule extends AbstractModule {
	@Override
	protected void configure() {
        bindScope(RequestScoped.class, Scopes.NO_SCOPE);
        bindInterceptor(Matchers.any(), Matchers.annotatedWith(RequireAuthenticated.class),
                new AuthenticationChecker.CheckIsAuthenticated(getProvider(SessionState.class)));
        bindInterceptor(Matchers.any(), Matchers.annotatedWith(RequireAdmin.class),
                new AuthenticationChecker.CheckIsAdmin(getProvider(SessionState.class)));
	}
}
