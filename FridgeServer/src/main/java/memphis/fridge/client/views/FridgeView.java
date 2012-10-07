package memphis.fridge.client.views;

import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 8/10/12
 */
public interface FridgeView extends IsWidget {

	AcceptsOneWidget getUserPanel();

	AcceptsOneWidget getProductsPanel();

}
