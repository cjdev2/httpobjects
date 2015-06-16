package org.httpobjects.client;

import org.httpobjects.Response;

public interface HttpClient {
	Response send(ClientRequest request);
}
