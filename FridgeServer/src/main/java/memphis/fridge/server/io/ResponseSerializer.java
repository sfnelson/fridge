package memphis.fridge.server.io;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 30/09/12
 */
public interface ResponseSerializer {
	void visitString(String name, String value);

	void visitInteger(String name, int value);
}
