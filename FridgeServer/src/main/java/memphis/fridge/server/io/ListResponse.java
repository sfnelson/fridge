package memphis.fridge.server.io;

import java.util.List;

import com.google.common.collect.Lists;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 7/10/12
 */
public class ListResponse<T extends Response> implements Response {

	private List<T> list;

	@SuppressWarnings("unchecked")
	protected ListResponse() {
		list = Lists.newArrayList();
	}

	protected ListResponse(List<T> values) {
		list = values;
	}

	protected void add(T value) {
		list.add(value);
	}

	public void visit(ResponseSerializer visitor) {
		visitor.visitList(this);
	}

	@SuppressWarnings("unchecked")
	public void visit(ResponseSerializer.ListSerializer visitor) {
		for (T elem : list) {
			if (elem instanceof ListResponse) {
				((ListResponse<?>) elem).visit(visitor.visitSublist());
			} else {
				((ObjectResponse) elem).visit(visitor.visitObject());
			}
		}
	}
}
