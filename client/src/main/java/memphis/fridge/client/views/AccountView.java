package memphis.fridge.client.views;

import com.google.gwt.user.client.ui.IsWidget;

import memphis.fridge.client.rpc.Account;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 10/10/12
 */
public interface AccountView extends IsWidget {

	void setPresenter(Presenter presenter);

	void setUsername(String username);

	void setDetails(Account account);

	interface Presenter {
		void logout();
	}
}
