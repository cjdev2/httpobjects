package org.httpobjects.client;

import org.httpobjects.Response;

/**
 * WARNING: This API is highly experimental.  This means it should be considered unstable at present; if you build on it, you may have to change your code a lot going forward.
 */
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
