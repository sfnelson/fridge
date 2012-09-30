package memphis.fridge.server;

import java.io.IOException;
import java.util.Scanner;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import memphis.fridge.server.io.Request;
import memphis.fridge.server.io.Response;
import memphis.fridge.server.services.RequestService;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 28/09/12
 */
@Singleton
public class RestServlet extends HttpServlet {

	@Inject
	RequestService service;

	@Override
	protected void doGet(final HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Scanner sc = new Scanner(req.getPathInfo());
		sc.useDelimiter("/");
		final String base = sc.next();
		final String method = sc.next();
		final String format = sc.next();
		final String request = sc.next();

		Response response = service.visitRequest(new Request() {
			public String getRequest() {
				return request;
			}

			public String getParameter(String name) {
				return req.getParameter(name);
			}
		});


	}


}
