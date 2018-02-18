package org.httpobjects.representation;

import org.httpobjects.Representation;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/** @deprecated not lazy, left for bwards compat, use ImmutableRep */
@Deprecated
public final class LazyImmutableRep implements Representation {

    private ImmutableRep self;

    public LazyImmutableRep(String contentType, InputStream data) {
        this.self = new ImmutableRep(contentType, data);
    }

    public byte[] head(int n) {
        byte[] got = get();
        int k = got.length;
        int m = Integer.min(k, n);
        byte[] give = new byte[m];
        for (int i = 0; i < m; i++) {
            give[i] = got[i];
        }
        return give;
    }

    public byte[] get() {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            self.write(out);
            out.close();
            return out.toByteArray();
        } catch (IOException err) {
            throw new RuntimeException(err);
        }
    }

    @Override
    public String contentType() {
        return self.contentType();
    }

    @Override
    public void write(OutputStream out) {
        self.write(out);
    }

    @Override
    public String show() {
        String show = self.show();
        if (show.length() <= 80 ) return show;
        else return show.substring(0, 77) + "...";
    }

    @Override
    public boolean eq(Representation that) {
        return self.eq(that);
    }
}
