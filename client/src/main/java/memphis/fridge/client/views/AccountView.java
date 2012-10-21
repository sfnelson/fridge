package memphis.fridge.client.views;

import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.IsWidget;

import memphis.fridge.client.rpc.Messages;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 10/10/12
 */
public interface AccountView extends IsWidget {

	void setPresenter(Presenter presenter);

	void setUsername(String username);

	void setDetails(Messages.Account account);

	void setStoreDetails(boolean storeDetails);

	void clearChildren();

	interface Presenter {
		void store();

		void clear();

		void topup(AcceptsOneWidget container);

		void transfer(AcceptsOneWidget container);

		void logout();
	}
}
