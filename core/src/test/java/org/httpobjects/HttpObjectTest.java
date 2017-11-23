package org.httpobjects;

import org.httpobjects.header.request.RequestHeader;
import org.httpobjects.path.Path;
import org.httpobjects.util.HttpObjectUtil;
import org.httpobjects.util.Method;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.httpobjects.DSL.*;
import static org.junit.Assert.*;

public class HttpObjectTest {

    @Test
    public void maskShouldUseThePathToSelectBetweenResources() throws Exception {
        // given
        Response leftResponse = OK(Text("left response"));
        Response rightResponse = OK(Text("right response"));
        HttpObject left = new HttpObject("/left", leftResponse);
        HttpObject right = new HttpObject("/right", rightResponse);
        HttpObject masked = left.mask(right);
        Request leftRequest = request("/left", Method.GET);
        Request rightRequest = request("/right", Method.GET);

        // when
        Response maybeLeftResponse = masked.get(leftRequest);
        Response maybeRightResponse = masked.get(rightRequest);

        // then
        assertEquals(leftResponse, maybeLeftResponse);
        assertEquals(rightResponse, maybeRightResponse);
    }

    @Test
    public void maskShouldReturnNOT_FOUNDWhenPathMatchingFailsOnBothResources() throws Exception {
        // given
        Response leftResponse = OK(Text("left response"));
        Response rightResponse = OK(Text("right response"));
        HttpObject left = new HttpObject("/left", leftResponse);
        HttpObject right = new HttpObject("/right", rightResponse);
        Representation repr = Text("Not Found");
        HttpObject masked = left.mask(right, repr);
        Request request = request("/middle", Method.GET);

        // when
        Response maybe404 = masked.get(request);

        // then
        assertEquals(ResponseCode.NOT_FOUND, maybe404.code());
        assertEquals("Not Found",
                HttpObjectUtil.toUtf8(maybe404.representation()));
    }

    @Test
    public void decorateShouldApplyDecoratorOnEvents() throws Exception {
        // given
        SampleEvents decorator = new SampleEvents();
        HttpObject resource = new HttpObject("/", allowed(Method.GET, Method.POST)) {
            @Override public Response get(Request req) { return OK(Text("You Got!")); }
            @Override public Response post(Request req) { return OK(Text("You Post!")); }
        };
        HttpObject decorated = resource.onEvents(decorator);
        Request getReq = request("/", Method.GET);
        Request postReq = request("/", Method.POST);

        // when
        Response getRes = decorated.get(getReq);
        Response postRes = decorated.post(postReq);

        // then
        assertEquals("Request: 1, " + getReq, decorator.log.get(0));
        assertEquals("Response: 1, " + getRes, decorator.log.get(1));
        assertEquals("Request: 2, " + postReq, decorator.log.get(2));
        assertEquals("Response: 2, " + postRes, decorator.log.get(3));
    }

    @Test
    public void decorateShouldRethrowErrors() throws Exception {
        // given
        SampleEvents decorator = new SampleEvents();
        RuntimeException error = new RuntimeException();
        HttpObject resource = new HttpObject("/", allowed(Method.GET)) {
            @Override public Response get(Request req) { throw error; }
        };
        HttpObject decorated = resource.onEvents(decorator);
        Object result;

        // when
        try { result = resource.get(request("/", Method.GET)); }
        catch (Throwable err) { result = err; }

        // then
        assertEquals(error, result);
    }

    private Request request(String path, Method method) {
        return new Request() {
            @Override public Query query() { return new Query(""); }
            @Override public Path path() { return new Path(path); }
            @Override public RequestHeader header() { return new RequestHeader(); }
            @Override public ConnectionInfo connectionInfo() {
                return new ConnectionInfo("10.10.10.10", 40,
                        "20.20.20.20", 80);
            }
            @Override public boolean hasRepresentation() { return false; }
            @Override public Representation representation() { return null; }
            @Override public Request immutableCopy() { return this; }
            @Override public Method method() { return method; }
        };
    }

    private class SampleEvents implements HttpObject.Events<Integer> {

        AtomicInteger atomicEventId = new AtomicInteger();

        List<String> log = new ArrayList<>();

        @Override
        public Integer onRequest(Request request) {
            Integer eventId = atomicEventId.incrementAndGet();
            log.add("Request: " + eventId + ", " + request);
            return eventId;
        }

        @Override
        public void onResponse(Integer integer, Response response) {
            log.add("Response: " + integer + ", " + response);
        }

        @Override
        public void onError(Throwable error) {
            log.add("Error: " + error);
        }
    }
}
