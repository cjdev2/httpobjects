package org.httpobjects.representation;

import org.httpobjects.Representation;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import static java.nio.charset.StandardCharsets.UTF_8;
import java.util.ArrayList;

public final class LazyImmutableRep implements Representation {

    private String contentType;
    private InputStream data;
    private ArrayList<Byte> representation;
    private boolean done;

    public LazyImmutableRep(String contentType, InputStream data) {
        this.contentType = contentType;
        this.data = data;
        this.representation = new ArrayList<>();
        this.done = false;
    }

    public byte[] head(int n) {

        int k = representation.size();
        for (int i = 0; i < n - k; i++) getOne(0);

        int j = Math.min(n, representation.size());
        byte[] result = new byte[j];
        for (int i = 0; i < j; i++) result[i] = representation.get(i);
        return result;
    }

    public byte[] get() {
        force();
        return head(representation.size());
    }

    @Override
    public String contentType() {
        return contentType;
    }

    @Override
    public void write(OutputStream out) {
        force();
        for (byte b : representation) writeOne(out, b, 0);
    }

    @Override
    public String show() {
        String str = new String(head(80), UTF_8);
        if (done) return str;
        else return str.substring(0, 77) + "...";
    }

    @Override
    public boolean eq(Representation that) {
        if (that instanceof LazyImmutableRep) {
            String left = new String(this.get(), UTF_8);
            String right = new String(((LazyImmutableRep) that).get(), UTF_8);
            return left.equals(right);
        } else return false;
    }

    private void getOne(int tries) {
        if (!done) {
            try {
                int b = data.read();
                if (b == -1) { done = true; data.close(); }
                else representation.add((byte) b);
            } catch (IOException err) {
                if (tries > 10) throw new RuntimeException(err);
                else getOne(tries + 1);
            }
        }
    }

    private void force() {
        while (!done) getOne(0);
    }

    private static void writeOne(OutputStream out, byte b, int tries) {
        try { out.write(b); } catch (IOException err) {
            if (tries > 10) throw new RuntimeException(err);
            else writeOne(out, b, tries + 1);
        }
    }
}
