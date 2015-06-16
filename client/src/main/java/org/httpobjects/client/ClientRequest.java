package org.httpobjects.client;

import org.httpobjects.Representation;
import org.httpobjects.header.HeaderField;
import org.httpobjects.header.request.RequestHeader;
import org.httpobjects.util.Method;

/**
 * 
 * TODO: Replace this with the Request class (which is unfortunately currently too tied to the server)
 *
 */
public class ClientRequest {
	private final Method method;
	private final String uri;
	private final RequestHeader header;
	private final Representation representation;
	
	public ClientRequest(Method method, String uri) {
		this(method, uri, null);
	}
	public ClientRequest(Method method, String uri, Representation representation, HeaderField ... fields) {
		this.method = method;
		this.uri = uri;
		this.header = new RequestHeader(fields);
		this.representation = representation;
	}

	public Method method() {
		return method;
	}

	public String uri() {
		return uri;
	}

	public RequestHeader header() {
		return header;
	}

	public Representation representation() {
		return representation;
	}
	
}