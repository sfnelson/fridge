package memphis.fridge.test;

import memphis.fridge.test.ioc.GuiceMockProvider;
import org.easymock.EasyMock;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 14/10/12
 */
public class GuiceEasymockProvider extends GuiceMockProvider {

	private boolean strict = false;

	public void setStrict(boolean strict) {
		this.strict = strict;
	}

	@SuppressWarnings("unchecked")
	protected <T> T createMock(final Class<T> type) {
		return strict ? EasyMock.createStrictMock(type) : EasyMock.createMock(type);
	}

	@Override
	protected void after() {
		reset();
		super.after();
	}

	public void reset() {
		for (Object mock : getMocks()) {
			if (strict) EasyMock.resetToStrict(mock);
			else EasyMock.resetToDefault(mock);
		}
	}

	public void replay() {
		for (Object mock : getMocks()) {
			EasyMock.replay(mock);
		}
	}

	public void verify() {
		for (Object mock : getMocks()) {
			EasyMock.verify(mock);
		}
	}
}
