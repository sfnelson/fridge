package memphis.fridge.server.io;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Map;

import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.Message;
import com.googlecode.protobuf.format.JsonFormat;
import javax.inject.Inject;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import memphis.fridge.server.ioc.SessionState;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 11/10/12
 */
@Provider
@Produces(MediaType.APPLICATION_JSON)
public class SignedJsonMessageWriter<T> implements MessageBodyWriter<T> {

	@Inject
	javax.inject.Provider<SessionState> session;

	public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return mediaType.isCompatible(MediaType.APPLICATION_JSON_TYPE);
	}

	public long getSize(T t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return -1;
	}

	public void writeTo(T t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
						MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
			throws IOException, WebApplicationException {


		if (!GeneratedMessage.class.isAssignableFrom(type)) {
			throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
		}

		String message = JsonFormat.printToString((Message) t);

		/* if authenticated then sign the message */
		if (session.get().isAuthenticated()) {
			Map<String, String> headers = session.get().sign(message);
			for (Map.Entry<String, String> e : headers.entrySet()) {
				httpHeaders.putSingle(e.getKey(), e.getValue());
			}
		}

		entityStream.write(message.getBytes("utf-8"));
	}
}
