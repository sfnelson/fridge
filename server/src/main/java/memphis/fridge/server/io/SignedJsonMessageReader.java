package memphis.fridge.server.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;

import com.google.common.io.CharStreams;
import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.GeneratedMessage;
import com.googlecode.protobuf.format.JsonFormat;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.MessageBodyReader;
import memphis.fridge.server.ioc.SessionState;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 11/10/12
 */
@Singleton
@javax.ws.rs.ext.Provider
@Produces(MediaType.APPLICATION_JSON)
public class SignedJsonMessageReader<T> implements MessageBodyReader<T> {

	@Inject
	Provider<SessionState> session;

	@Inject
	ExtensionRegistry extensionRegistry;

	public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return mediaType.isCompatible(MediaType.APPLICATION_JSON_TYPE);
	}

	public T readFrom(Class<T> type, Type genericType, Annotation[] annotations, MediaType mediaType,
					  MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
			throws IOException, WebApplicationException {
		if (!GeneratedMessage.class.isAssignableFrom(type)) {
			throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
		}

		String message = CharStreams.toString(new InputStreamReader(entityStream));

		for (Annotation a : annotations) {
			if (a instanceof Signed) {
				/* check for and validate signed messages */
				session.get().authenticate((Signed) a, httpHeaders, message);
			}
		}

		GeneratedMessage.Builder builder;
		try {
			builder = (GeneratedMessage.Builder) (type.getMethod("newBuilder").invoke(type));
		} catch (NoSuchMethodException ex) {
			throw new WebApplicationException(ex, Response.Status.INTERNAL_SERVER_ERROR);
		} catch (IllegalAccessException ex) {
			throw new WebApplicationException(ex, Response.Status.INTERNAL_SERVER_ERROR);
		} catch (InvocationTargetException ex) {
			throw new WebApplicationException(ex, Response.Status.INTERNAL_SERVER_ERROR);
		}

		JsonFormat.merge(message, extensionRegistry, builder);

		return (T) builder.build();
	}
}
