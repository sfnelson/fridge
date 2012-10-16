package memphis.fridge.client.rpc;

/**
 * @author stephen
 */
public interface Account {

    String getUsername();

    String getRealName();

    String getEmail();

    int getBalance();

    boolean isAdmin();

    boolean isGrad();

    boolean isEnabled();

}
