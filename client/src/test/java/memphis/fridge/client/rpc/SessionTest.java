package memphis.fridge.client.rpc;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 7/10/12
 */
/* TODO can't use this until I work out how to reference MockInjectingRunner
@RunWith(MockInjectingRunner.class)
@MockInjectingRunner.ToInject({Session.class})
public class SessionTest {

	public static final String username = "USERNAME";
	public static final String password = "PASSWORD";
	public static final String pwhash = "PWHASH";
	public static final String snonce = "SNONCE";

	@Inject
	Session session;

	@Inject
	MockInjectingRunner.MockManager m;

	@Before
	public void setUp() throws Exception {
		m.reset();
	}

	@After
	public void tearDown() throws Exception {
		m.verify();
	}

	@Test
	public void testLogin() throws Exception {
		Capture<RequestNonce.Handler> handler = new Capture<RequestNonce.Handler>();
		Capture<Scheduler.ScheduledCommand> command = new Capture<Scheduler.ScheduledCommand>();
		expect(session.crypt.md5(password)).andReturn(pwhash);
		m.getMock(RequestNonce.class).requestNonce(eq(username), capture(handler));
		m.getMock(Scheduler.class).scheduleDeferred(capture(command));
		session.place.goTo(anyObject(SessionPlace.class));
		m.replay();
		session.login(username, password);
		handler.getValue().onNonceReceived(snonce);
		command.getValue().execute();
	}

	@Test
	public void testLogout() throws Exception {
		m.replay();
	}

	@Test
	public void testSign() throws Exception {
		m.replay();
	}

	@Test
	public void testGetNonce() throws Exception {
		m.replay();
	}

	@Test
	public void testNonceReady() throws Exception {
		m.replay();
	}
}        */
