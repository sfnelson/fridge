package memphis.fridge.client.rpc;

import java.util.AbstractList;
import java.util.Arrays;
import java.util.List;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.safehtml.shared.UriUtils;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 22/10/12
 */
public class Messages {

	public static interface Account {

		String getUsername();

		String getRealName();

		String getEmail();

		int getBalance();

		boolean isAdmin();

		boolean isGrad();

		boolean isEnabled();

	}

	public static interface Stock {
		String getProductCode();

		String getDescription();

		int getInStock();

		String getInStockText();

		int getPrice();

		String getPriceText();

		String getCategory();

		int getCategoryOrder();

		SafeUri getImageHref();
	}

	public static interface InfoResponse {
		boolean hasNumServed();

		int getNumServed();

		boolean hasMotd();

		int getMotd();

		List<String> getAdmins();

		List<String> getNaughtyPeople();
	}

	public static interface TransactionResponse {
		int getBalance();

		int getCost();
	}

	public static interface StockResponse {
		List<? extends Stock> getStock();
	}

	public static interface PurchaseRequest {
		void addProduct(String code, int number);

		JSONObject toJSON();
	}

	public static JSONObject createTransferRequest(String fromUser, String toUser, int amount) {
		TransferRequestJS rq = create();
		rq.init(fromUser, toUser, amount);
		return new JSONObject(rq);
	}

	public static JSONObject createTopupRequest(int amount) {
		TopupRequestJS rq = create();
		rq.init(amount);
		return new JSONObject(rq);
	}

	public static PurchaseRequest createPurchaseRequest() {
		PurchaseRequestJS req = create();
		return req;
	}

	public static TransactionResponse parseTransactionResponse(String json) {
		TransactionResponseJS resp = JsonUtils.safeEval(json);
		return resp;
	}

	public static Account parseAccountResponse(String json) {
		AccountJS resp = JsonUtils.safeEval(json);
		return resp;
	}

	public static StockResponse parseStockResponse(String json) {
		StockResponseJS resp = JsonUtils.safeEval(json);
		return resp;
	}

	public static InfoResponse parseInfoResponse(String json) {
		InfoResponseJS resp = JsonUtils.safeEval(json);
		return resp;
	}

	private static <T extends JavaScriptObject> T create() {
		return (T) JavaScriptObject.createObject();
	}

	private static class JsList<T extends JavaScriptObject> extends AbstractList<T> {
		private final JsArray<T> data;

		public JsList(JsArray<T> data) {
			this.data = data;
		}

		@Override
		public T get(int index) {
			return data.get(index);
		}

		@Override
		public int size() {
			return data.length();
		}
	}

	private static final class AccountRequestJS extends JavaScriptObject {
		protected AccountRequestJS() {
		}

		public native void init(String username) /*-{
			this.username = username;
		}-*/;

		public native int getUsername() /*-{
			return this.username;
		}-*/;
	}

	private static final class AccountJS extends JavaScriptObject implements Account {
		protected AccountJS() {
		}

		public native String getUsername() /*-{
			return this.username;
		}-*/;

		public native String getRealName() /*-{
			return this.full_name;
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

	private static final class TransferRequestJS extends JavaScriptObject {
		protected TransferRequestJS() {
		}

		public native void init(String fromUser, String toUser, int amount) /*-{
			this.from_user = fromUser;
			this.to_user = toUser;
			this.amount = amount;
		}-*/;

		public native String getFromUser() /*-{
			return this.from_user;
		}-*/;

		public native String getToUser() /*-{
			return this.to_user;
		}-*/;

		public native int getAmount() /*-{
			return this.amount;
		}-*/;
	}

	private static final class PurchaseRequestJS extends JavaScriptObject implements PurchaseRequest {
		protected PurchaseRequestJS() {
		}

		@Override
		public native void addProduct(String code, int quantity) /*-{
			this.orders = this.orders || [];
			this.orders.push({'code':code, 'quantity':quantity });
		}-*/;

		@Override
		public JSONObject toJSON() {
			return new JSONObject(this);
		}
	}

	private static final class TopupRequestJS extends JavaScriptObject {
		protected TopupRequestJS() {
		}

		public native void init(int amount) /*-{
			this.amount = amount;
		}-*/;

		public native int getAmount() /*-{
			return this.amount;
		}-*/;
	}

	private static final class TransactionResponseJS extends JavaScriptObject implements TransactionResponse {
		protected TransactionResponseJS() {
		}

		public native int getBalance() /*-{
			return this.balance;
		}-*/;

		public native int getCost() /*-{
			return this.cost;
		}-*/;
	}

	private static final class StockResponseJS extends JavaScriptObject implements StockResponse {
		protected StockResponseJS() {
		}

		@Override
		public List<? extends Stock> getStock() {
			return new JsList<StockJS>(_getStock());
		}

		private native JsArray<StockJS> _getStock() /*-{
			return this.stock;
		}-*/;
	}

	private static final class StockJS extends JavaScriptObject implements Stock {
		protected StockJS() {
		}

		public native String getProductCode() /*-{
			return this.product_code;
		}-*/;

		public native String getDescription() /*-{
			return this.description;
		}-*/;

		public native int getInStock() /*-{
			return this.in_stock;
		}-*/;

		public native String getInStockText() /*-{
			return "" + this.in_stock;
		}-*/;

		public native int getPrice() /*-{
			return this.price;
		}-*/;

		public String getPriceText() {
			int price = getPrice();
			int mod = price % 100;
			return "$" + price / 100 + "." + (mod < 10 ? "0" : "") + mod;
		}

		public native String getCategory() /*-{
			return this.category;
		}-*/;

		public native int getCategoryOrder() /*-{
			return this.category_order;
		}-*/;

		public SafeUri getImageHref() {
			return UriUtils.fromTrustedString("/memphis/fridge/rest/images?product="
					+ UriUtils.encode(getProductCode()));
		}
	}

	private static final class InfoResponseJS extends JavaScriptObject implements InfoResponse {
		protected InfoResponseJS() {
		}

		@Override
		public native boolean hasNumServed() /*-{
			return this.hasOwnProperty('num_served');
		}-*/;

		@Override
		public native int getNumServed() /*-{
			return this.num_served;
		}-*/;

		@Override
		public native boolean hasMotd() /*-{
			return this.hasOwnProperty('motd');
		}-*/;

		@Override
		public native int getMotd() /*-{
			return this.motd;
		}-*/;

		@Override
		public List<String> getAdmins() {
			String[] admins = _getAdmins();
			if (admins == null) admins = new String[0];
			return Arrays.asList(admins);
		}

		private native String[] _getAdmins() /*-{
			if (!this.hasOwnProperty('admins')) return null;
			else return this.admins;
		}-*/;

		@Override
		public List<String> getNaughtyPeople() {
			String[] naughty = _getNaughtyPeople();
			if (naughty == null) naughty = new String[0];
			return Arrays.asList(naughty);
		}

		private native String[] _getNaughtyPeople() /*-{
			if (!this.hasOwnProperty('naughty_people')) return null;
			return this.naughty_people;
		}-*/;
	}
}
