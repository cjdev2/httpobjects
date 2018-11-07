package org.httpobjects.representation;

import org.httpobjects.Representation;
import org.httpobjects.util.HttpObjectUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class ImmutableRep implements Representation {

    private final String contentType;
    private final byte[] representation;

    public ImmutableRep(String contentType, InputStream data) {
        try {
            this.contentType = contentType;
            java.io.ByteArrayOutputStream buf = new ByteArrayOutputStream();
            byte[] b = new byte[256];
            int n;
            while ((n = data.read(b)) != -1) {
                buf.write(b, 0, n);
            }
            this.representation = buf.toByteArray();
            data.close();
        } catch (IOException err) {
            throw new RuntimeException(err);
        }
    }

    @Override
    public String contentType() {
        return contentType;
    }

    @Override
    public void write(OutputStream out) {
        try {
            out.write(representation);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String show() {
        return HttpObjectUtil.toUtf8(this);
    }

    @Override
    public boolean eq(Representation that) {
        return this.show().equals(that.show());
    }
}
