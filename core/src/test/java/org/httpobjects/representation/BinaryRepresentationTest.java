package org.httpobjects.representation;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.junit.Test;

public class BinaryRepresentationTest {
    @Test
    public void leavesInputExceptionsAlone() {
        // given
        final RuntimeException connectionException = new RuntimeException("boo!");
        final InputStream in = new InputStream() {
            @Override
            public int read() throws IOException {
                throw connectionException;
            }
        };
        BinaryRepresentation r = new BinaryRepresentation("foo/bar", in);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        
        // when
        Throwable err = null;
        try {
            r.write(out);
        } catch (Exception e) {
            err = e;
        }
        
        // then
        assertNotNull(err);
        assertTrue(err  == connectionException);
        err.printStackTrace();
    }
    
    @Test
    public void wrapsOutputExceptionsWithAHelpfulMessage() {
        // given
        final EOFException connectionException = new EOFException();
        BinaryRepresentation r = new BinaryRepresentation("foo/bar", new ByteArrayInputStream("foobar".getBytes()));
        OutputStream out = new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                throw connectionException;
            }
        };
        
        // when
        Throwable err = null;
        try {
            r.write(out);
        } catch (Exception e) {
            err = e;
        }
        
        // then
        assertNotNull(err);
        assertEquals("Error writing representation.  This is probably because the connection to the remote host was closed.", err.getMessage());
        assertTrue(err.getCause() == connectionException);
        err.printStackTrace();
    }

}
