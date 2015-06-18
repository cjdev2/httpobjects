package org.httpobjects.client;

import org.httpobjects.Representation;
import org.httpobjects.header.HeaderField;
import org.httpobjects.header.request.RequestHeader;

/**
 * 
 * TODO: Replace this with the Request class (which is, unfortunately, currently tied to the server-side paradigm)
 *
 */
public class ClientRequest {
	private final String query;
	private final RequestHeader header;
	private final Representation representation;
	

	public ClientRequest(HeaderField ... fields) {
		this("", null, fields);
	}
	
	public ClientRequest(String query, HeaderField ... fields) {
		this(query, null, fields);
	}
	public ClientRequest(Representation representation, HeaderField ... fields) {
		this("", representation, fields);
	}
	public ClientRequest(String query, Representation representation, HeaderField ... fields) {
		this.query = query;
		this.header = new RequestHeader(fields);
		this.representation = representation;
	}
	
	public String query() {
		return query;
	}
	
	public RequestHeader header() {
		return header;
	}

	public Representation representation() {
		return representation;
	}
	
}