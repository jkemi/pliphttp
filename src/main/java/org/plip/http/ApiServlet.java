package org.plip.http;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ApiServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static class RootHolder {
        private static final AbstractEndpoint ROOT = new AbstractEndpoint();

		public static AbstractEndpoint getInstance() {
			return ROOT;
		}
    }

	private final AbstractEndpoint root;

	public ApiServlet() {
		root = RootHolder.getInstance();
	}

	public static void addEndpoint(EndpointFilter e) {
		RootHolder.getInstance().addEndpoint(e);
	}

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		CharSequence path = req.getPathInfo();
		if (path == null) {
			path = "";
		}

		final ServletContext ctx = getServletContext();
		Endpoint endpoint = root.canService(path, req, resp, ctx);
		if (endpoint != null) {
			endpoint.service(path, req, resp, ctx);
		} else {
			resp.sendError(HttpServletResponse.SC_NOT_FOUND);
		}
	}
}
