package memphis.fridge.server.io;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 30/09/12
 */
public interface ResponseSerializer<O, L> {

	interface ListSerializer {
		ObjectSerializer visitObject();

		ListSerializer visitSublist();
	}

	interface ObjectSerializer {
		void visitString(String name, String value);

		void visitInteger(String name, int value);

		void visitBoolean(String name, boolean value);

		ListSerializer visitList(String name);
	}

	O visitObject(ObjectResponse response);

	<T extends Response> L visitList(ListResponse<T> response);
}
