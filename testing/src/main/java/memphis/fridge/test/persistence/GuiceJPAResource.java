package memphis.fridge.test.persistence;

import com.google.common.collect.Maps;
import com.google.inject.BindingAnnotation;
import com.google.inject.Injector;
import com.google.inject.persist.PersistService;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import org.junit.rules.ExternalResource;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 14/10/12
 */
@Singleton
public class GuiceJPAResource extends ExternalResource {

    private static Logger log = Logger.getLogger(GuiceJPATestWatcher.class.getSimpleName());
    private static Map<Class, Injector> injectorMap = Maps.newHashMap();

    @Target({ElementType.FIELD, ElementType.PARAMETER})
    @Retention(RetentionPolicy.RUNTIME)
    @BindingAnnotation
    public static @interface TestClass {}

    @Inject
    public GuiceJPAResource(@TestClass Class<?> testClass, Injector injector) {
        injectorMap.put(testClass, injector);
    }

    @Override
    public Statement apply(Statement base, Description description) {
        if (description.isTest() || description.isSuite()) {
            ResourceStatement statement = new ResourceStatement(base);
            injectorMap.get(description.getTestClass()).injectMembers(statement);
            return statement;
        }
        return base;
    }

    private static class ResourceStatement extends Statement {

        @Inject
        PersistService ps;

        // em is not available until ps.start() has been called
        @Inject
        Provider<EntityManager> em;

        @Inject
        @TestClass
        Class<?> testClass;

        private Statement inner;

        public ResourceStatement(Statement inner) {
            this.inner = inner;
        }

        @Override
        public void evaluate() throws Throwable {
            ps.start();
            checkForTestData();
            try {
                inner.evaluate();
            } finally {
                ps.stop();
            }
        }

        private void checkForTestData() throws Throwable {
            if (testClass.isAnnotationPresent(WithTestData.class)) {
                EntityTransaction tx = em.get().getTransaction();
                tx.begin();
                testClass.getAnnotation(WithTestData.class).value()
                        .newInstance().injectData(em.get());
                tx.commit();
            }
        }
    }
}
