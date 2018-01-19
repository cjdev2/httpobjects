package org.httpobjects.jetty;

import org.httpobjects.*;
import org.httpobjects.client.ApacheCommons4xHttpClient;
import org.httpobjects.client.HttpClient;
import org.httpobjects.util.Method;

import static org.httpobjects.DSL.Text;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

import java.net.ServerSocket;

public class HttpObjectsJettyHandlerTest {

    private int findFreePort() {
        try {
            ServerSocket serverSocket = new ServerSocket(0);
            int port = serverSocket.getLocalPort();
            serverSocket.close();
            return port;

        } catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    @Test
    public void requestBodyShouldBeReusable() {
        // given
        HttpObject resource = new HttpObject("/", DSL.allowed(Method.POST)) {
            @Override
            public Response post(Request req) {
                System.out.print(req.show());
                if (req.body().get().equals("body")) return OK(Text("We did it!"));
                else return BAD_REQUEST();
            }
        };
        int port = findFreePort();
        HttpClient client = new ApacheCommons4xHttpClient();

        // when
        HttpObjectsJettyHandler.launchServer(port, resource);
        Response result = client.resource("http://localhost:" + port).post(Text("body"));
        // we make a post request to this server with body "body"

        // then
        assertEquals(ResponseCode.OK, result.code());
    }
}
