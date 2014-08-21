package org.plip.http;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface EndpointFilter {
	Endpoint canService(CharSequence path, HttpServletRequest req, HttpServletResponse res, ServletContext ctx);
}
