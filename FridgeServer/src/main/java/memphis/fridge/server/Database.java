package memphis.fridge.server;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 28/09/12
 */
public interface Database {

	void deleteOldNonces();

	int countNonceOccurances(String clientNonce);

	void createNonce(String serverNonce, String clientNonce);

	int authenticateUser(String username, String hMAC, String passwordMD5);

}
