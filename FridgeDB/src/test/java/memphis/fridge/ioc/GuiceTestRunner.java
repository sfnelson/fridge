package memphis.fridge.ioc;

import java.lang.annotation.*;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.Lists;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 29/09/12
 */
public class GuiceTestRunner extends BlockJUnit4ClassRunner {

	@Target(ElementType.TYPE)
	@Retention(RetentionPolicy.RUNTIME)
	@Inherited
	public @interface GuiceModules {
		GuiceModule[] value();
	}

	@Target(ElementType.TYPE)
	@Retention(RetentionPolicy.RUNTIME)
	@Inherited
	public @interface GuiceModule {
		Class<? extends Module> value();

		String[] args() default {};
	}

	protected final Injector injector;

	public GuiceTestRunner(Class<?> klass) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, InitializationError {
		super(klass);
		injector = Guice.createInjector(getModules(klass));
	}

	@Override
	protected Object createTest() throws Exception {
		Object test = super.createTest();
		injector.injectMembers(test);
		return test;
	}

	private Iterable<? extends Module> getModules(Class<?> klass) throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
		GuiceModules annotation = klass.getAnnotation(GuiceModules.class);
		if (annotation == null) return Collections.emptyList();
		List<Module> modules = Lists.newArrayList();
		for (GuiceModule a : annotation.value()) {
			Module m;
			if (a.args() == null || a.args().length == 0) m = a.value().newInstance();
			else {
				Class<?>[] params = new Class<?>[a.args().length];
				for (int i = 0; i < params.length; i++) {
					params[i] = String.class;
				}
				m = a.value().getConstructor(params).newInstance((Object[]) a.args());
			}
			modules.add(m);
		}
		return modules;
	}
}
