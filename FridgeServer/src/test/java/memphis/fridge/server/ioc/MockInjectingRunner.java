package memphis.fridge.server.ioc;

import java.lang.annotation.*;
import java.lang.reflect.Field;
import java.util.Map;

import com.google.common.collect.Maps;
import com.google.inject.*;
import org.easymock.EasyMock;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 6/10/12
 */
public class MockInjectingRunner extends BlockJUnit4ClassRunner {

	private static final boolean STRICT = true;

	@Target(ElementType.TYPE)
	@Retention(RetentionPolicy.RUNTIME)
	@Inherited
	public @interface ToInject {
		Class<?>[] value();
	}

	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	@Inherited
	public @interface Mock {
	}

	public interface MockManager {
		void reset();

		void replay();

		void verify();
	}

	public MockInjectingRunner(Class<?> klass) throws InitializationError {
		super(klass);
	}

	@Override
	protected Object createTest() throws Exception {
		Class<?> toTest = getTestClass().getJavaClass();
		MockModule mocker = new MockModule(toTest, toTest.getAnnotation(ToInject.class).value());
		Injector injector = Guice.createInjector(mocker);
		return injector.getInstance(toTest);
	}

	private static class MockModule extends AbstractModule implements MockManager {

		private final Class<?> toTest;
		private final Class<?>[] toMock;
		private Map<Class<?>, Object> mocked = Maps.newHashMap();

		public MockModule(Class<?> toTest, Class<?>[] toMock) {
			this.toTest = toTest;
			this.toMock = toMock;
		}

		@Override
		protected void configure() {
			bind(MockManager.class).toInstance(this);
			for (Field f : toTest.getDeclaredFields()) {
				if (f.getAnnotation(Mock.class) != null) {
					createMock(f.getType());
				}
			}
			for (Class<?> c : toMock) {
				for (Field f : c.getDeclaredFields()) {
					if (f.getAnnotation(Inject.class) != null
							|| f.getAnnotation(javax.inject.Inject.class) != null) {
						if (!mocked.containsKey(f.getType())) {
							createMock(f.getType());
						}
					}
				}
			}
		}

		@SuppressWarnings("unchecked")
		private <T> void createMock(final Class<T> type) {
			T mock = STRICT ? EasyMock.createStrictMock(type) : EasyMock.createMock(type);
			mocked.put(type, mock);
			bind(type).toProvider(new Provider<T>() {
				public T get() {
					return (T) mocked.get(type);
				}
			});
		}

		public void reset() {
			for (Object mock : mocked.values()) {
				if (STRICT) EasyMock.resetToStrict(mock);
				else EasyMock.resetToDefault(mock);
			}
		}

		public void replay() {
			for (Object mock : mocked.values()) {
				EasyMock.replay(mock);
			}
		}

		public void verify() {
			for (Object mock : mocked.values()) {
				EasyMock.verify(mock);
			}
		}
	}
}
