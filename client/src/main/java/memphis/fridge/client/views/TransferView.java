package memphis.fridge.client.views;

import com.google.gwt.user.client.ui.IsWidget;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 22/10/12
 */
public interface TransferView extends IsWidget {

	void setPresenter(Presenter presenter);

	interface Presenter {
		void transfer(String toUsername, int cents);
	}
}
