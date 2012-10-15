package memphis.fridge.test;

import javax.inject.Inject;
import memphis.fridge.test.persistence.GuiceJPAResource;
import memphis.fridge.test.persistence.GuiceJPATestWatcher;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 14/10/12
 */
@RunWith(GuiceTestRunner.class)
public abstract class GuiceJPATest {

	@ClassRule
	@Inject
	public static GuiceJPAResource _jpaResource;

	@Rule
	@Inject
	public GuiceJPATestWatcher _watcher;
}
