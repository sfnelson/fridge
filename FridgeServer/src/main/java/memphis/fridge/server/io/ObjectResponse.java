package memphis.fridge.server.io;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 7/10/12
 */
public abstract class ObjectResponse implements Response {

	public final void visit(ResponseSerializer visitor) {
		visitor.visitObject(this);
	}

	public abstract void visit(ResponseSerializer.ObjectSerializer visitor);
}
