package memphis.fridge.server.ioc;

import javax.inject.Provider;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceException;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 22/10/12
 */
public interface TransactionChecker {

	static class CheckInTransaction implements MethodInterceptor {
		private final Provider<EntityManager> em;

		public CheckInTransaction(Provider<EntityManager> em) {
			this.em = em;
		}

		public Object invoke(MethodInvocation invocation) throws Throwable {
			EntityTransaction tx = em.get().getTransaction();
			if (tx == null || !tx.isActive()) {
				throw new PersistenceException("requires transaction");
			}
			return invocation.proceed();
		}
	}
}
