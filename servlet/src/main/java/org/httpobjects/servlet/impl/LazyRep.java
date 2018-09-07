package org.httpobjects.servlet.impl;

import org.httpobjects.Representation;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class LazyRep implements Representation {

    private final String contentType;
    private final InputStream input;
    private byte[] data;

    public LazyRep(String contentType, InputStream inputStream) {
        this.contentType = contentType;
        this.input = inputStream;
        this.data = null;
    }

    private byte[] getData() throws Exception {
        if (data == null) {
            if (input == null) {
                data = new byte[0];
            } else {
                byte[] b = new byte[256];
                ByteArrayOutputStream buf = new ByteArrayOutputStream();
                int n;
                while ((n = input.read(b)) != -1) {
                    buf.write(b, 0, n);
                }
                input.close();
                data = buf.toByteArray();
            }
        }
        return data;
    }

    @Override
    public String contentType() {
        return contentType;
    }

    @Override
    public void write(OutputStream out) {
        try {
            out.write(getData());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String toString() {
        return "LazyRep(" + contentType + "," + (data == null ? "[not-yet-read]" : new String(data, StandardCharsets.UTF_8)) + ")";
    }
}
