package memphis.fridge.server.services;

import javax.inject.Inject;
import memphis.fridge.domain.Product;
import memphis.fridge.domain.User;
import memphis.fridge.exceptions.FridgeException;
import memphis.fridge.server.io.ListResponse;
import memphis.fridge.server.io.ResponseSerializer;
import memphis.fridge.server.ioc.MockInjectingRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static memphis.fridge.server.TestingData.*;
import static memphis.fridge.utils.CurrencyUtils.toCents;
import static org.easymock.EasyMock.expect;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 7/10/12
 */
@RunWith(MockInjectingRunner.class)
@MockInjectingRunner.ToInject({GetProducts.class})
public class GetProductsTest {

	@Inject
	GetProducts p;

	@Inject
	MockInjectingRunner.MockManager m;

	@Inject
	@MockInjectingRunner.Mock
	User u;

	@Inject
	@MockInjectingRunner.Mock
	ResponseSerializer.ListSerializer resp;

	@Inject
	@MockInjectingRunner.Mock
	ResponseSerializer.ObjectSerializer os;

	@Before
	public void setUp() throws Exception {
		m.reset();
	}

	@Test
	public void testGetProductsNoUser() throws Exception {
		expect(p.fridge.getGraduateDiscount()).andReturn(GRAD_TAX);
		expect(p.products.getEnabledProducts()).andReturn(products());
		expectCoke(false);
		expectCookie(false);
		test(null);
	}

	@Test
	public void testGetProductsGrad() throws Exception {
		expect(p.fridge.getGraduateDiscount()).andReturn(GRAD_TAX);
		expect(p.products.getEnabledProducts()).andReturn(products());
		expect(p.users.retrieveUser(USERNAME)).andReturn(u);
		expect(u.isGrad()).andReturn(true);
		expectCoke(true);
		expectCookie(true);
		test(USERNAME);
	}

	@Test
	public void testGetProductsUGrad() throws Exception {
		expect(p.fridge.getGraduateDiscount()).andReturn(GRAD_TAX);
		expect(p.products.getEnabledProducts()).andReturn(products());
		expect(p.users.retrieveUser(USERNAME)).andReturn(u);
		expect(u.isGrad()).andReturn(false);
		expectCoke(false);
		expectCookie(false);
		test(USERNAME);
	}

	private void test(String username) {
		m.replay();
		try {
			ListResponse<?> r = p.getProducts(username);
			r.visit(resp);
		} catch (FridgeException ex) {
			m.verify();
			throw ex;
		}
		m.verify();
	}

	private void expectCoke(boolean isGrad) {
		Product coke = coke();
		expect(resp.visitObject()).andReturn(os);
		os.visitString("product_code", coke.getProductCode());
		os.visitString("description", coke.getDescription());
		os.visitInteger("in_stock", coke.getInStock());
		os.visitInteger("price", toCents(cokeTotal(isGrad)));
		os.visitString("category", coke.getCategory().getTitle());
		os.visitInteger("category_order", coke.getCategory().getDisplaySequence());
	}

	private void expectCookie(boolean isGrad) {
		Product cookie = cookie();
		expect(resp.visitObject()).andReturn(os);
		os.visitString("product_code", cookie.getProductCode());
		os.visitString("description", cookie.getDescription());
		os.visitInteger("in_stock", cookie.getInStock());
		os.visitInteger("price", toCents(cookieTotal(isGrad)));
		os.visitString("category", cookie.getCategory().getTitle());
		os.visitInteger("category_order", cookie.getCategory().getDisplaySequence());
	}
}
