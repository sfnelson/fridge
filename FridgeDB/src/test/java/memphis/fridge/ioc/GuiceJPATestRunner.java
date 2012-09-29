package memphis.fridge.ioc;

import java.lang.annotation.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Logger;

import com.google.inject.persist.PersistService;
import com.google.inject.persist.UnitOfWork;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import org.junit.Test;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 29/09/12
 */
public class GuiceJPATestRunner extends GuiceTestRunner {

	@Target(ElementType.METHOD)
	@Retention(RetentionPolicy.RUNTIME)
	@Inherited
	public @interface Rollback {
	}

	PersistService ps;

	public GuiceJPATestRunner(Class<?> klass) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, InitializationError {
		super(klass);
	}

	@Override
	public void run(RunNotifier notifier) {
		setUpJpa();

		super.run(notifier);

		tearDownJpa();
	}

	private void setUpJpa() {
		ps = injector.getInstance(PersistService.class);
		ps.start();
	}

	private void tearDownJpa() {
		ps.stop();
	}

	@Override
	protected Statement methodBlock(FrameworkMethod method) {
		final Statement result = super.methodBlock(method);

		if (method.getAnnotation(Rollback.class) != null
				&& method.getAnnotation(Test.class) != null) {
			RollbackStatement rs = injector.getInstance(RollbackStatement.class);
			rs.setDelegate(result, method.getMethod());
			return rs;
		} else {
			return result;
		}
	}

	static class RollbackStatement extends Statement {

		private final Logger log = Logger.getLogger(GuiceJPATestRunner.class.getSimpleName());

		@Inject
		UnitOfWork context;

		@Inject
		Provider<EntityManager> em;

		Statement delegate;
		Method method;

		public void setDelegate(Statement delegate, Method method) {
			this.delegate = delegate;
			this.method = method;
		}

		@Override
		public void evaluate() throws Throwable {
			log.entering(method.getDeclaringClass().getSimpleName(), method.getName());

			context.begin();
			try {
				EntityTransaction tx = em.get().getTransaction();
				if (!tx.isActive()) tx.begin();
				tx.setRollbackOnly();
				try {
					delegate.evaluate();
				} finally {
					tx.rollback();
				}
			} finally {
				context.end();
				log.exiting(method.getDeclaringClass().getSimpleName(), method.getName());
			}

		}
	}
}
