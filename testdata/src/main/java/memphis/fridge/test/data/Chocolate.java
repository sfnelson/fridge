package memphis.fridge.test.data;

import memphis.fridge.domain.ProductCategory;

/**
* @author stephen
*/
public class Chocolate {
    public static final int ID = 2;
    public static final int ORDER = 3;
    public static final String TITLE = "Chocolate";

    public static ProductCategory create() {
        return new ProductCategory(ID, ORDER, TITLE);
    }
}
