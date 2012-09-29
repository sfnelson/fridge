package memphis.fridge.ioc;

import java.lang.annotation.*;
import java.lang.reflect.InvocationTargetException;

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
			rs.setDelegate(result);
			return rs;
		} else {
			return result;
		}
	}

	static class RollbackStatement extends Statement {

		@Inject
		UnitOfWork context;

		@Inject
		Provider<EntityManager> em;

		Statement delegate;

		public void setDelegate(Statement delegate) {
			this.delegate = delegate;
		}

		@Override
		public void evaluate() throws Throwable {
			context.begin();

			EntityTransaction tx = em.get().getTransaction();
			if (!tx.isActive()) tx.begin();
			tx.setRollbackOnly();
			delegate.evaluate();
			tx.rollback();

			context.end();
		}
	}
}
