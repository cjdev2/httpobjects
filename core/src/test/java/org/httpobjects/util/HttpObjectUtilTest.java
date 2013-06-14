package org.httpobjects.util;

import static org.httpobjects.DSL.OK;
import static org.httpobjects.DSL.Text;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.httpobjects.HttpObject;
import org.httpobjects.Request;
import org.httpobjects.Response;
import org.httpobjects.test.MockRequest;
import org.junit.Test;

public class HttpObjectUtilTest {

    class PatchTestingObject extends HttpObject {
        final Response response;
        final List<Request> requestsRecieved = new ArrayList<Request>();
        
        public PatchTestingObject(String pathPattern, Response response) {
            super(pathPattern);
            this.response = response;
        }

        @Override
        public Response patch(Request req) {
            requestsRecieved.add(req);
            return response;
        }
    }
    
    @Test
    public void pipesInputsAndOutputsToThePatchMethod() {
        // given
        final Response expectedResponse = OK(Text("Hello WOrld"));
        final PatchTestingObject o = new PatchTestingObject("/foo", expectedResponse);
        
        final Request input = new MockRequest(o, "/foo");
        
        // when
        Response result = HttpObjectUtil.invokeMethod(o, Method.PATCH, input);
        
        // then
        assertNotNull(result);
        assertTrue(expectedResponse == result);
        assertEquals(1, o.requestsRecieved.size());
        assertTrue(input == o.requestsRecieved.get(0));
        
    }
}
