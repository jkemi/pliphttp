package org.plip.http;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface Endpoint {
	void service(CharSequence path, HttpServletRequest req, HttpServletResponse resp, ServletContext ctx) throws IOException, ServletException;
}
