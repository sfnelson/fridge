package memphis.fridge.test;

import javax.inject.Inject;
import javax.inject.Provider;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 14/10/12
 */
@RunWith(GuiceTestRunner.class)
public class MokitoTest {

	@Inject
	@ClassRule
	public static GuiceMockitoProvider mocks;

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
		when(mockMe.isMocked()).thenReturn(true);
		assertTrue(mockMe.isMocked());

		assertNotNull(hasEntityManager);
		assertNotNull(hasEntityManager.m);
		hasEntityManager.m.call();
		verify(hasEntityManager.m).call();

		mocks.reset();

		assertNotNull(hasEntityManagerProvider);
		assertNotNull(hasEntityManagerProvider.mp);
		assertNotNull(hasEntityManagerProvider.mp.get());
		hasEntityManagerProvider.mp.get().call();
		verify(hasEntityManagerProvider.mp.get()).call();

		assertSame(hasEntityManager.m, hasEntityManagerProvider.mp.get());
	}
}
