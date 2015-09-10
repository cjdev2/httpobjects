package org.httpobjects.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;

import org.httpobjects.Representation.Chunk;

public class StubChunk implements Chunk {

    @Override
    public void writeInto(ByteBuffer buffer) {
        throw notImplemented();
    }

    @Override
    public void writeInto(WritableByteChannel out) {
        throw notImplemented();
    }

    @Override
    public void writeInto(OutputStream out) {
        throw notImplemented();
    }

    @Override
    public void writeInto(byte[] buffer, int offset) {
        throw notImplemented();
    }

    @Override
    public byte[] toNewArray() {
        throw notImplemented();
    }

    @Override
    public int size() {
        throw notImplemented();
    }

    private RuntimeException notImplemented() {
        return new RuntimeException("NOT IMPLEMENTED");
    }
}
