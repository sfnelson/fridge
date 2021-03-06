package memphis.fridge.server.services;

import java.math.BigDecimal;

import javax.inject.Inject;

import memphis.fridge.dao.ProductsDAO;
import memphis.fridge.dao.PurchaseDAO;
import memphis.fridge.test.data.Coke;
import memphis.fridge.test.data.Cookie;
import memphis.fridge.test.data.TwoCokesAndACookie;
import memphis.fridge.domain.Product;
import memphis.fridge.domain.User;
import memphis.fridge.exceptions.*;
import memphis.fridge.protocol.Messages;
import memphis.fridge.server.ioc.AuthModule;
import memphis.fridge.server.ioc.SessionState;
import memphis.fridge.test.*;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runners.model.Statement;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;
import static memphis.fridge.test.data.Utils.*;
import static memphis.fridge.utils.CurrencyUtils.fromCents;
import static memphis.fridge.utils.CurrencyUtils.toCents;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 6/10/12
 */
@RunWith(GuiceTestRunner.class)
@TestModule(AuthModule.class)
public class PurchaseTest {

    @ClassRule
    @Inject
    public static GuiceMockitoProvider mocks;

    @Rule
    public TestRule setup = new TestRule() {
        @Override
        public Statement apply(final Statement base, final Description description) {
            return new Statement() {
                @Override
                public void evaluate() throws Throwable {
                    String name = description.getMethodName();
                    if (!description.isTest());
                    else if (name.endsWith("Undergrad")) setUp(true, false);
                    else if (name.endsWith("Grad")) setUp(true, true);
                    else setUp(false, false);
                    base.evaluate();
                }
            };
        }
    };

	@Inject
    @InjectMocks
	Purchase p;

	@Inject
	@Mock
	User user;

	@Inject
	@Mock
	SessionState s;

    boolean isGrad = false;

    /** called from setup rule using method name to determine action */
	public void setUp(boolean isAuthenticated, boolean isGrad) {
		mocks.reset();

        when(s.isAuthenticated()).thenReturn(isAuthenticated);

        this.isGrad = isGrad;
        when(s.getUser()).thenReturn(user);
        when(user.isGrad()).thenReturn(isGrad);
        when(user.getUsername()).thenReturn(USERNAME);
        when(p.fridge.getGraduateDiscount()).thenReturn(GRAD_TAX);

        when(p.products.findProduct(Coke.CODE)).thenReturn(Coke.create());
        when(p.products.findProduct(Cookie.CODE)).thenReturn(Cookie.create());
	}

	@Test(expected = AuthenticationException.class)
	public void testPurchaseNotAuthenticated() throws Exception {
		when(s.isAuthenticated()).thenReturn(false);

        p.purchase(order());
	}

	@Test(expected = InvalidProductException.class)
	public void testPurchaseInvalidProductGrad() throws Exception {
		when(p.products.findProduct(Coke.CODE)).thenThrow(new InvalidProductException(Coke.CODE));

        p.purchase(order());
	}

	@Test(expected = InsufficientStockException.class)
	public void testPurchaseInsufficientStockGrad() throws Exception {
		Product coke = Coke.create();
		coke.setInStock(1);

		when(p.products.findProduct(Coke.CODE)).thenReturn(coke);
        p.purchase(order());
	}

	@Test(expected = InvalidProductException.class)
	public void testPurchaseInvalidProductTwoGrad() throws Exception {
        when(p.products.findProduct(Cookie.CODE)).thenThrow(new InvalidProductException(Cookie.CODE));

        try {
            p.purchase(order());
        }
        finally {
            Cokes.verifyPurchase(p.products, p.purchases, user);
        }
	}

	@Test(expected = InsufficientFundsException.class)
	public void testPurchaseInsufficientFundsUndergrad() throws Exception {
        doThrow(new InsufficientFundsException(USERNAME, BigDecimal.ZERO))
                .when(p.users).removeFunds(user, TwoCokesAndACookie.total(isGrad));

        try {
            p.purchase(order());
        }
        finally {
            verifyProductUpdates();
            verify(p.users).removeFunds(user, TwoCokesAndACookie.total(isGrad));
        }
	}

	@Test(expected = InsufficientFundsException.class)
	public void testPurchaseInsufficientFundsGrad() throws Exception {
        doThrow(new InsufficientFundsException(USERNAME, BigDecimal.ZERO))
                .when(p.users).removeFunds(user, TwoCokesAndACookie.total(isGrad));

        try {
            p.purchase(order());
        }
        finally {
            verifyProductUpdates();
            verify(p.users).removeFunds(user, TwoCokesAndACookie.total(isGrad));
        }
	}

	@Test
	public void testPurchaseGrad() throws Exception {
        final BigDecimal initialBalance = fromCents(1000);
        final BigDecimal cost = TwoCokesAndACookie.total(isGrad);
        final BigDecimal finalBalance = initialBalance.subtract(cost);

        when(p.users.retrieveUser(USERNAME)).thenReturn(user);
        when(user.getBalance()).thenReturn(finalBalance);

        Messages.TransactionResponse response = p.purchase(order());

        verifyProductUpdates();
        verify(p.users).removeFunds(user, cost);
        verify(p.creditLog).createPurchase(user, cost);

		assertEquals(toCents(finalBalance), response.getBalance());
		assertEquals(toCents(cost), response.getCost());
	}

	@Test
	public void testPurchaseUndergrad() throws Exception {
		final BigDecimal initialBalance = fromCents(1000);
		final BigDecimal cost = TwoCokesAndACookie.total(isGrad);
        final BigDecimal finalBalance = initialBalance.subtract(cost);

        when(p.users.retrieveUser(USERNAME)).thenReturn(user);
        when(user.getBalance()).thenReturn(finalBalance);

        Messages.TransactionResponse response = p.purchase(order());

        verifyProductUpdates();
        verify(p.users).removeFunds(user, cost);
        verify(p.creditLog).createPurchase(user, cost);

		assertEquals(toCents(finalBalance), response.getBalance());
		assertEquals(toCents(cost), response.getCost());
	}

	private void verifyProductUpdates() {
        Cokes.verifyPurchase(p.products, p.purchases, user);
        Cookies.verifyPurchase(p.products, p.purchases, user);
	}

	private void verifyUserUpdates(BigDecimal cost) {
		verify(p.users).removeFunds(user, cost);
		verify(p.creditLog).createPurchase(user, cost);
	}

    private static Messages.PurchaseRequest order() {
        Messages.PurchaseRequest.Builder rq = Messages.PurchaseRequest.newBuilder();
        rq.addOrdersBuilder().setCode(Coke.CODE).setQuantity(Cokes.NUM);
        rq.addOrdersBuilder().setCode(Cookie.CODE).setQuantity(Cookies.NUM);
        return rq.build();
    }


    static class Cokes extends TwoCokesAndACookie.Cokes {
        public static memphis.fridge.domain.Purchase createPurchase(PurchaseDAO p, User user, boolean isGrad) {
            return p.createPurchase(user, Coke.create(), NUM, baseCost(), taxCost(isGrad));
        }
        public static void verifyPurchase(ProductsDAO products, PurchaseDAO purchases, User user) {
            Mockito.verify(products).consumeProduct(Coke.create(), NUM);
            Mockito.verify(purchases).createPurchase(user, Coke.create(), NUM, baseCost(), taxCost(user.isGrad()));
        }
    }

    static class Cookies extends TwoCokesAndACookie.Cookies {
        public static memphis.fridge.domain.Purchase createPurchase(PurchaseDAO p, User user, boolean isGrad) {
            return p.createPurchase(user, Coke.create(), NUM, baseCost(), taxCost(isGrad));
        }
        public static void verifyPurchase(ProductsDAO products, PurchaseDAO purchases, User user) {
            Mockito.verify(products).consumeProduct(Cookie.create(), NUM);
            Mockito.verify(purchases).createPurchase(user, Cookie.create(), NUM, baseCost(), taxCost(user.isGrad()));
        }
    }
}
