package memphis.fridge.server.ioc;

import javax.inject.Provider;
import memphis.fridge.exceptions.AccessDeniedException;
import memphis.fridge.exceptions.AuthenticationException;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 11/10/12
 */
interface AuthenticationChecker {

	static class CheckIsAuthenticated implements MethodInterceptor {
		private final Provider<SessionState> session;

		CheckIsAuthenticated(Provider<SessionState> session) {
			this.session = session;
		}

		public Object invoke(MethodInvocation invocation) throws Throwable {
			if (session.get().isAuthenticated())
				return invocation.proceed();
			else
				throw new AuthenticationException();
		}
	}

	static class CheckIsAdmin implements MethodInterceptor {
		private final Provider<SessionState> session;

		CheckIsAdmin(Provider<SessionState> session) {
			this.session = session;
		}

		public Object invoke(MethodInvocation invocation) throws Throwable {
            SessionState session = this.session.get();
			if (!session.isAuthenticated())
				throw new AuthenticationException();
			else if (!session.isAdmin())
				throw new AccessDeniedException(session.getUser().getUsername());
			else
				return invocation.proceed();
		}
	}
}
