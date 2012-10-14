package memphis.fridge.test;

import javax.inject.Inject;
import javax.inject.Provider;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.*;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 14/10/12
 */
@RunWith(GuiceTestRunner.class)
public class EasymockTest {

	@Inject
	@ClassRule
	public static GuiceEasymockProvider mocks;

	interface MockMe {
		boolean isMocked();
	}

	interface Mocked {
		void call();
	}

	static class HasMock {
		@Inject
		Mocked m;
	}

	static class HasMockProvider {
		@Inject
		Provider<Mocked> mp;
	}

	@Inject
	@Mock
	public MockMe mockMe;

	@Inject
	@InjectMocks
	public HasMock hasEntityManager;

	@Inject
	@InjectMocks
	public HasMockProvider hasEntityManagerProvider;

	@Test
	public void testTestMockInjection() throws Exception {
		assertNotNull(mocks);

		assertNotNull(mockMe);
		expect(mockMe.isMocked()).andReturn(true);

		mocks.replay();

		assertTrue(mockMe.isMocked());

		mocks.verify();
		mocks.reset();

		assertNotNull(hasEntityManager);
		assertNotNull(hasEntityManager.m);

		hasEntityManager.m.call();

		mocks.replay();

		hasEntityManager.m.call();

		mocks.verify();
		mocks.reset();

		assertNotNull(hasEntityManagerProvider);
		assertNotNull(hasEntityManagerProvider.mp);
		assertNotNull(hasEntityManagerProvider.mp.get());
		hasEntityManagerProvider.mp.get().call();

		mocks.replay();

		hasEntityManagerProvider.mp.get().call();

		mocks.verify();

		assertSame(hasEntityManager.m, hasEntityManagerProvider.mp.get());
	}
}
