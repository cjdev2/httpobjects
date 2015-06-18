package org.httpobjects.client;

import org.httpobjects.Response;

public interface HttpClient {
	RemoteObject resource(String uri);
	
	public interface RemoteObject {
	    Response delete();
	    Response delete(ClientRequest req);
	    Response get();
	    Response get(ClientRequest req);
	    Response head();
	    Response head(ClientRequest req);
	    Response options();
	    Response options(ClientRequest req);
	    Response post();
	    Response post(ClientRequest req);
	    Response put();
	    Response put(ClientRequest req);
	    Response patch();
	    Response patch(ClientRequest req);
	}
}
