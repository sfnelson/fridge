package memphis.fridge.server.io;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 29/09/12
 */
public interface Response {

	void visitResponse(ResponseSerializer visitor);

}
