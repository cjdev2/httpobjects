package org.httpobjects.representation;

import org.httpobjects.Representation;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import static java.nio.charset.StandardCharsets.UTF_8;

import static org.junit.Assert.*;

public class LazyImmutableRepTest {

    @Test
    public void headTest() throws Exception {
        // given
        LazyImmutableRep rep = new LazyImmutableRep(text, in("some awesome text"));

        // then
        assertEquals("some aweso", string(rep.head(10)));
        assertEquals("some ", string(rep.head(5)));
        assertEquals("some awesome te", string(rep.head(15)));
        assertEquals("some awesome text", string(rep.head(20)));
    }

    @Test
    public void getTest() throws Exception {
        // given
        LazyImmutableRep rep = new LazyImmutableRep(text, in("some meh text"));

        // then
        assertEquals("some meh text", string(rep.get()));
        assertEquals("some meh text", string(rep.get()));
    }

    @Test
    public void contentTypeTest() throws Exception {
        // given
        String contentType = String.valueOf(Math.random());
        LazyImmutableRep rep = new LazyImmutableRep(contentType, in("foo"));

        // then
        assertEquals(contentType, rep.contentType());
    }

    @Test
    public void writeTest() throws Exception {
        // given
        LazyImmutableRep rep = new LazyImmutableRep(text, in("bar"));
        ByteArrayOutputStream out1 = new ByteArrayOutputStream();
        ByteArrayOutputStream out2 = new ByteArrayOutputStream();

        // when
        rep.write(out1);
        rep.write(out2);

        // then
        assertEquals("bar", string(out1.toByteArray()));
        assertEquals("bar", string(out2.toByteArray()));
    }

    @Test
    public void showTest() throws Exception {
        // given
        LazyImmutableRep rep = new LazyImmutableRep(text, in("baz"));
        String bigString = "Lorem ipsum dolor sit amet, " +
                "consectetur adipiscing elit, " +
                "sed do eiusmod tempor incididunt " +
                "ut labore et dolore magna aliqua.";
        LazyImmutableRep bigRep = new LazyImmutableRep(text, in(bigString));

        // when
        String show1 = rep.show();
        String show2 = rep.show();
        String show3 = bigRep.show();

        // then
        assertEquals("baz", show1);
        assertEquals("baz", show2);
        assertEquals(bigString.substring(0, 77) + "...", show3);
    }

    @Test
    public void eqTest() throws Exception {
        // given
        LazyImmutableRep us1 = new LazyImmutableRep(text, in("foo"));
        LazyImmutableRep us2 = new LazyImmutableRep(text, in("foo"));
        LazyImmutableRep us3 = new LazyImmutableRep(text, in("bar"));
        Representation them = new BinaryRepresentation(text, in("foo"));

        // then
        assertEquals(true, us1.eq(us1));
        assertEquals(true, us1.eq(us2));
        assertEquals(false, us1.eq(us3));
        assertEquals(false, us1.eq(them));
        assertEquals(false, them.eq(us1));
    }

    private static String text = "text/plain";

    private static InputStream in(String str) {
        return new ByteArrayInputStream(str.getBytes(UTF_8));
    }

    private static String string(byte[] bytes) {
        return new String(bytes, UTF_8);
    }
}
