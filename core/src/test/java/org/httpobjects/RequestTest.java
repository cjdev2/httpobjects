package org.httpobjects;

import org.httpobjects.header.request.RequestHeader;
import org.httpobjects.path.Path;
import org.httpobjects.util.Method;
import org.junit.Test;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.junit.Assert.*;

public class RequestTest {

    @Test
    public void bodyShouldReturnAStringOfTheRepresentationWhenPresent() throws Exception {
        // given
        Request req = fooRequest();

        // then
        assertEquals(Optional.of("foo bar"), req.body(StandardCharsets.UTF_8));
    }

    @Test
    public void bodyShouldBeInvokableWithoutACharset() throws Exception {
        // given
        Request req = fooRequest();

        // then
        assertEquals(Optional.of("foo bar"), req.body());
    }

    @Test
    public void bodyShouldReturnAnEmptyOptionalWhenTheRepresentationIsNotPresent() throws Exception {
        // given
        Request req = barRequest();

        // then
        assertEquals(Optional.<String>empty(), req.body());
    }

    private Request fooRequest() {
        return new Request() {
            @Override public Query query() { return new Query("?foo=bar"); }
            @Override public Path path() { return new Path("/foo/bar/"); }
            @Override public RequestHeader header() { return new RequestHeader(); }
            @Override public ConnectionInfo connectionInfo() {
                return new ConnectionInfo("10.10.10.10", 40,
                        "20.20.20.20", 80);
            }
            @Override public boolean hasRepresentation() { return true; }
            @Override public Representation representation() { return new Representation() {
                @Override public String contentType() { return "text/plain"; }
                @Override public void write(OutputStream out) {
                    try { out.write("foo bar".getBytes()); }
                    catch (Throwable err) { throw new RuntimeException(err); };
                }
            }; }
            @Override public Request immutableCopy() { return this; }
            @Override public Method method() { return Method.POST; }
        };
    }

    private Request barRequest() {
        return new Request() {
            @Override public Query query() { return new Query("?foo=bar"); }
            @Override public Path path() { return new Path("/foo/bar/"); }
            @Override public RequestHeader header() { return new RequestHeader(); }
            @Override public ConnectionInfo connectionInfo() {
                return new ConnectionInfo("10.10.10.10", 40,
                        "20.20.20.20", 80);
            }
            @Override public boolean hasRepresentation() { return false; }
            @Override public Representation representation() { return null; }
            @Override public Request immutableCopy() { return this; }
            @Override public Method method() { return Method.POST; }
        };
    }
}
