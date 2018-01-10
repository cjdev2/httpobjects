package org.httpobjects;

import org.httpobjects.header.GenericHeaderField;
import static org.httpobjects.DSL.*;

import org.junit.Test;

import static org.junit.Assert.*;

public class ResponseTest {

    @Test
    public void showTest() throws Exception {
        // given
        Response res = OK(Text("body"),
                new GenericHeaderField("foocience", "foo"),
                new GenericHeaderField("bariness", "bar"));
        String expected =
                "Response(code = OK(200),header = {bariness:\"bar\",foocience:\"foo\"},representation = body)";

        // when
        String result1 = res.show();
        String result2 = res.show();

        // then
        assertEquals(expected, result1);
        assertEquals(expected, result2);
    }

    @Test
    public void eqTest() throws Exception {
        // given
        Response res1 = OK(Text("body"),
                new GenericHeaderField("foocience", "foo"),
                new GenericHeaderField("bariness", "bar"));
        Response res2 = OK(Text("body"),
                new GenericHeaderField("foocience", "foo"),
                new GenericHeaderField("bariness", "bar"));
        Response res3 = OK(Text("doby"),
                new GenericHeaderField("foocience", "fuzz"),
                new GenericHeaderField("bariness", "barre"));

        // then
        assertEquals(true, res1.eq(res1));
        assertEquals(true, res1.eq(res2));
        assertEquals(false, res1.eq(res3));
    }
}
