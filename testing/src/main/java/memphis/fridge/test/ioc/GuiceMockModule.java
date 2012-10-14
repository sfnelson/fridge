package memphis.fridge.test.ioc;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Map;

import com.google.common.collect.Maps;
import com.google.inject.AbstractModule;
import com.google.inject.Provider;
import memphis.fridge.test.InjectMocks;
import memphis.fridge.test.Mock;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 14/10/12
 */
public class GuiceMockModule extends AbstractModule {

	static class MockProxy {

		GuiceMockProvider provider = null;

		void setProvider(GuiceMockProvider provider) {
			this.provider = provider;
		}

		<T> T getMock(Class<T> type) {
			if (provider == null) {
				throw new RuntimeException("No GuiceMockProvider instance bound, have you requested one?");
			} else {
				return provider.getMock(type);
			}
		}
	}

	private final Class<?>[] toInject;
	private final MockProxy proxy;
	private final Map<Class<?>, Provider<?>> mockProviders = Maps.newHashMap();

	public GuiceMockModule(Class<?>... toInject) {
		this.toInject = toInject;
		proxy = new MockProxy();
	}

	@Override
	protected void configure() {
		bind(MockProxy.class).toInstance(proxy);
		for (Class<?> c : toInject) {
			for (Field f : c.getDeclaredFields()) {
				if (f.getAnnotation(Mock.class) != null) {
					createMock(getInjectionType(f));
				}
				if (f.getAnnotation(InjectMocks.class) != null) {
					injectMocks(getInjectionType(f));
				}
			}
		}
	}

	private void injectMocks(Class<?> target) {
		for (Field f : target.getDeclaredFields()) {
			if (f.getAnnotation(com.google.inject.Inject.class) != null
					|| f.getAnnotation(javax.inject.Inject.class) != null) {
				createMock(getInjectionType(f));
			}
		}
	}

	private <T> void createMock(final Class<T> type) {
		if (!mockProviders.containsKey(type)) {
			Provider<T> provider = new Provider<T>() {
				@Override
				public T get() {
					return proxy.getMock(type);
				}
			};
			mockProviders.put(type, provider);
			bind(type).toProvider(provider);
		}
	}

	@SuppressWarnings("unchecked")
	private static Class<?> getInjectionType(Field f) {
		if (f.getType().equals(javax.inject.Provider.class)) {
			ParameterizedType t = (ParameterizedType) f.getGenericType();
			return (Class<?>) t.getActualTypeArguments()[0];
		}
		return f.getType();
	}
}
