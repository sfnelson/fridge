package memphis.fridge.client.events;

import com.google.web.bindery.event.shared.Event;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;

import memphis.fridge.client.rpc.Account;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 10/10/12
 */
public class AccountEvent extends Event<AccountHandler> {

	public static final Type<AccountHandler> TYPE = new Type<AccountHandler>();

	public static HandlerRegistration register(EventBus eb, AccountHandler handler) {
		return eb.addHandler(TYPE, handler);
	}

	public static void fire(EventBus eb, Account account) {
		eb.fireEvent(new AccountEvent(account));
	}

	private final Account a;

	public AccountEvent(Account a) {
		this.a = a;
	}

	@Override
	public Type getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(AccountHandler handler) {
		handler.accountAvailable(a);
	}
}
