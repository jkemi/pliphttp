package org.plip.http;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AbstractEndpoint implements EndpointFilter {

	private final Pattern pattern;
	private final List<EndpointFilter> candidates;

	public AbstractEndpoint(Pattern pattern) {
		this.pattern = pattern;
		candidates = new ArrayList<>();
	}

	public AbstractEndpoint() {
		this((Pattern)null);
	}

	public  AbstractEndpoint(String regex, int flags) {
		this(Pattern.compile(regex, flags));
	}

	public AbstractEndpoint(String regex) {
		this(regex, 0);
	}

	public void addEndpoint(EndpointFilter e) {
		candidates.add(e);
	}

	@Override
	public Endpoint canService(	CharSequence path,
								HttpServletRequest req,
								HttpServletResponse resp,
								ServletContext ctx) {
		final Matcher m;
		final CharSequence rest;
		if (pattern != null) {
			m = pattern.matcher(path);
			if (!m.find()) {
				return null;
			}
			rest = path.subSequence(m.end(), path.length());
		} else {
			m = null;
			rest = path;
		}

		
		//ctx.log("trail: '" + rest.toString() + "'");

		for (EndpointFilter candidate : candidates) {
			Endpoint endpoint = candidate.canService(rest, req, resp, ctx);
			if (endpoint != null) {
				return endpoint;
			}
		}

		final CharSequence[] pathparams;
		if (m != null) {
			pathparams = new CharSequence[m.groupCount()];
			for (int i = 0; i < m.groupCount(); i++) {
				pathparams[i] = path.subSequence(m.start(i + 1), m.end(i + 1));
			}
		} else {
			pathparams = new CharSequence[0];
		}

		return new Endpoint() {

			@Override
			public void service(	CharSequence path,
									HttpServletRequest req,
									HttpServletResponse resp,
									ServletContext ctx
							) throws IOException,	ServletException {
				AbstractEndpoint.this.service(path, pathparams, req, resp, ctx);

			}
		};
	}

	public void service(CharSequence path, CharSequence[] pathparams, HttpServletRequest req,
				HttpServletResponse resp, ServletContext ctx) throws ServletException, IOException {
		resp.sendError(HttpServletResponse.SC_NOT_FOUND);
	}
}
