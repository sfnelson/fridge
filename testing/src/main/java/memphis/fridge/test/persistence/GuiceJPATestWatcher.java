package memphis.fridge.test.persistence;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import org.junit.internal.AssumptionViolatedException;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import java.util.logging.Logger;

import static org.junit.Assert.*;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 14/10/12
 */
public class GuiceJPATestWatcher extends TestWatcher {

    private static Logger log = Logger.getLogger(GuiceJPATestWatcher.class.getSimpleName());

	@Inject
	Provider<EntityManager> em;

	private EntityTransaction tx;

	@Override
	protected void starting(Description description) {
		assertNull(tx);
		tx = em.get().getTransaction();
		assertFalse(tx.isActive());
		tx.begin();
		tx.setRollbackOnly();

		WithTestData annotation = description.getAnnotation(WithTestData.class);
		if (annotation != null) {
			try {
				annotation.value().newInstance().injectData(em.get());
			} catch (InstantiationException e) {
				throw new AssumptionViolatedException("could not instantiate provider " + annotation.value().getName());
			} catch (IllegalAccessException e) {
				throw new AssumptionViolatedException("could not create provider " + annotation.value().getName());
			}
		}
	}

	@Override
	protected void finished(Description description) {
		assertNotNull(tx);
		assertTrue(tx.isActive());
		tx.rollback();
		tx = null;
	}
}
