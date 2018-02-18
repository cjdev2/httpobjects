package org.httpobjects.representation;

import org.httpobjects.Representation;
import org.httpobjects.util.HttpObjectUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class ImmutableRep implements Representation {

    private final String contentType;
    private final ArrayList<Byte> representation;

    public ImmutableRep(String contentType, InputStream data) {
        try {
            this.contentType = contentType;
            this.representation = new ArrayList<>();
            int b = data.read();
            while (b != -1) {
                representation.add((byte) b);
                b = data.read();
            }
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
        representation.forEach(b -> {
            try { out.write(b); }
            catch (IOException err) { throw new RuntimeException(err); }
        });
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
