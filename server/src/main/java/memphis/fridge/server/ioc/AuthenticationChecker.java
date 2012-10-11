package memphis.fridge.server.ioc;

import javax.inject.Provider;
import memphis.fridge.exceptions.AuthenticationException;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 11/10/12
 */
class AuthenticationChecker implements MethodInterceptor {

	private final Provider<SessionState> session;

	AuthenticationChecker(Provider<SessionState> session) {
		this.session = session;
	}

	public Object invoke(MethodInvocation invocation) throws Throwable {
		if (session.get().isAuthenticated())
			return invocation.proceed();
		else
			throw new AuthenticationException();
	}
}
