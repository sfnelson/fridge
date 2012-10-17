package memphis.fridge.test.data;

import memphis.fridge.domain.User;
import memphis.fridge.test.persistence.TestDataProvider;

import javax.persistence.EntityManager;

import static memphis.fridge.utils.CryptUtils.md5;
import static memphis.fridge.utils.CurrencyUtils.fromCents;

/**
* @author stephen
*/
public class Admin implements TestDataProvider {
    public static final String NAME = "admin";
    public static final String PASS = "pa55word";
    public static final String REAL = "Admin User";
    public static final String EMAIL = "admin@domain.com";
    public static final int BALANCE = 5000;

    @Override
    public void injectData(EntityManager em) {
        em.persist(create());
    }

    public static User create() {
        try {
            User user = new User(NAME, md5(PASS), REAL, EMAIL);
            user.setBalance(fromCents(BALANCE));
            user.setAdmin(true);
            user.setGrad(true);
            return user;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
