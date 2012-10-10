package memphis.fridge.client.rpc;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 10/10/12
 */
public final class Account extends JavaScriptObject {

	protected Account() {
	}

	public native String getUsername() /*-{
		return this.username;
	}-*/;

	public native String getRealName() /*-{
		return this.real_name;
	}-*/;

	public native String getEmail() /*-{
		return this.email;
	}-*/;

	public native int getBalance() /*-{
		return this.balance;
	}-*/;

	public native boolean isAdmin() /*-{
		return this.is_admin;
	}-*/;

	public native boolean isGrad() /*-{
		return this.is_grad;
	}-*/;

	public native boolean isEnabled() /*-{
		return this.is_enabled;
	}-*/;

	native String getHMAC() /*-{
		return this.hmac;
	}-*/;
}
