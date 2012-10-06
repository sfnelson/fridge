package memphis.fridge.server.io;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 7/10/12
 */
public interface Response {

	void visit(ResponseSerializer visitor);

}
