package memphis.fridge.test.data;

import memphis.fridge.domain.ProductCategory;
import memphis.fridge.test.persistence.TestDataProvider;

import javax.persistence.EntityManager;

/**
* @author stephen
*/
public class Drinks implements TestDataProvider {
    public static final int ID = 0;
    public static final int ORDER = 1;
    public static final String TITLE = "Drinks";

    public static ProductCategory create() {
        return new ProductCategory(ID, ORDER, TITLE);
    }

    @Override
    public void injectData(EntityManager em) {
        em.persist(create());
    }
}
