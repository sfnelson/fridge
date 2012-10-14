package memphis.fridge.test.persistence;

import com.google.inject.persist.PersistService;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import org.junit.rules.ExternalResource;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 14/10/12
 */
public class GuiceJPAResource extends ExternalResource {

	@Inject
	private PersistService ps;

	@Inject
	private Provider<EntityManager> em; // em is not available until ps.start() has been called

	private Class<?> testClass;

	@Override
	public Statement apply(Statement base, Description description) {
		this.testClass = description.getTestClass();

		return super.apply(base, description);
	}

	@Override
	protected void before() throws Throwable {
		ps.start();

		if (testClass.isAnnotationPresent(WithTestData.class)) {
			EntityTransaction tx = em.get().getTransaction();
			tx.begin();
			testClass.getAnnotation(WithTestData.class).value().newInstance().injectData(em.get());
			tx.commit();
		}
	}

	@Override
	protected void after() {
		ps.stop();
	}

}
