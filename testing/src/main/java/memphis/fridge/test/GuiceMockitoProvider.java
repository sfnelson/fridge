package memphis.fridge.test;

import memphis.fridge.test.ioc.GuiceMockProvider;
import org.mockito.Mockito;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 14/10/12
 */
public class GuiceMockitoProvider extends GuiceMockProvider {

	@Override
	protected <T> T createMock(Class<T> type) {
		return Mockito.mock(type);
	}

	@Override
	protected void after() {
		reset();
		super.after();
	}

	public void reset() {
		for (Object mock : getMocks()) {
			Mockito.reset(mock);
		}
	}
}
