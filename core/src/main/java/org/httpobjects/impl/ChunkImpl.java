package org.httpobjects.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;

import org.httpobjects.Representation.Chunk;

public class ChunkImpl extends StubChunk implements Chunk {
    public static final Chunk NULL_CHUNK = new ChunkImpl(new byte[]{}, 0, 0);

    private final byte[] buffer;
    private final int offset;
    private final int length;


    public ChunkImpl(byte[] buffer, int offset, int length) {
        super();
        this.buffer = buffer;
        this.offset = offset;
        this.length = length;
    }

    @Override
    public void writeInto(OutputStream out) {
        try {
            out.write(buffer, offset, length);
        } catch (IOException e) {
            throw makeException(e);
        }
    }


    protected RuntimeException makeException(Exception e){
        return new RuntimeException(e);
    }
}
