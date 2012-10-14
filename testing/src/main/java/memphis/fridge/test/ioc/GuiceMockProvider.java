package memphis.fridge.test.ioc;

import java.util.Map;
import java.util.logging.Logger;

import com.google.common.collect.Maps;
import javax.inject.Inject;
import org.junit.rules.ExternalResource;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 14/10/12
 */
public abstract class GuiceMockProvider extends ExternalResource {

	private static final Logger log = Logger.getLogger(GuiceMockProvider.class.getSimpleName());

	protected Class<?> testClass;
	protected Map<Class<?>, Object> mocks = Maps.newHashMap();

	@Inject
	private GuiceMockModule.MockProxy proxy;

	@Override
	public Statement apply(Statement base, Description description) {
		testClass = description.getTestClass();
		return super.apply(base, description);
	}

	@Override
	protected synchronized void before() throws Throwable {
		proxy.setProvider(this);
	}

	@Override
	protected void after() {
		proxy.setProvider(null);
	}

	protected abstract <T> T createMock(final Class<T> type);

	public <T> T getMock(Class<T> type) {
		if (!mocks.containsKey(type)) {
			log.finest("created mock: " + type.getSimpleName());
			T mock = createMock(type);
			mocks.put(type, mock);
			return mock;
		}
		return (T) mocks.get(type);
	}

	public Iterable<?> getMocks() {
		return mocks.values();
	}
}
