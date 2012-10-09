package memphis.fridge.client.views;

import com.google.gwt.user.client.ui.IsWidget;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 30/09/12
 */
public interface LoginView extends IsWidget {

	void setPresenter(Presenter p);

	String getUser();

	String getPass();

	public interface Presenter {
		void doLogin();
	}
}
