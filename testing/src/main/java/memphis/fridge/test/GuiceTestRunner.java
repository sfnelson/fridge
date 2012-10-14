package memphis.fridge.test;

import java.util.List;

import com.google.common.collect.Lists;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import memphis.fridge.test.ioc.GuiceMockModule;
import org.junit.runners.BlockJUnit4ClassRunner;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 29/09/12
 */
public class GuiceTestRunner extends BlockJUnit4ClassRunner {

	protected final Injector injector;

	public GuiceTestRunner(final Class<?> klass) throws Exception {
		super(klass);
		List<Module> modules = getModules(klass);
		modules.add(new AbstractModule() {
			@Override
			protected void configure() {
				requestStaticInjection(klass);
			}
		});
		modules.add(new GuiceMockModule(klass));
		injector = Guice.createInjector(modules);
	}

	@Override
	protected Object createTest() throws Exception {
		Object test = super.createTest();
		injector.injectMembers(test);
		return test;
	}

	private List<Module> getModules(Class<?> klass) throws Exception {
		List<Module> modules = Lists.newArrayList();
		if (klass.isAnnotationPresent(TestModules.class)) {
			TestModules annotation = klass.getAnnotation(TestModules.class);
			for (TestModule a : annotation.value()) {
				modules.add(parseModuleAnnotation(a));
			}
			if (klass.isAnnotationPresent(TestModule.class)) {
				throw new IllegalArgumentException(klass.getSimpleName()
						+ " is annotated with both @TestModules and @TestModule");
			}
		} else if (klass.isAnnotationPresent(TestModule.class)) {
			modules.add(parseModuleAnnotation(klass.getAnnotation(TestModule.class)));
		}
		return modules;
	}

	private Module parseModuleAnnotation(TestModule annotation) throws Exception {
		Module module;
		if (annotation.args() == null || annotation.args().length == 0) {
			module = annotation.value().newInstance();
		} else {
			Class<?>[] params = new Class<?>[annotation.args().length];
			for (int i = 0; i < params.length; i++) {
				params[i] = String.class;
			}
			module = annotation.value().getConstructor(params).newInstance((Object[]) annotation.args());
		}
		return module;
	}
}
