package org.httpobjects.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import org.httpobjects.Representation;
import org.httpobjects.Representation.ByteStream;
import org.httpobjects.Representation.Chunk;
import org.httpobjects.Stream;

public class ByteStreamImpl extends StreamImpl<Chunk> implements ByteStream {

    @Override
    public byte[] readIntoMemory() {
        return toByteArray(this);
    }

    @Override
    public String readIntoString(String charset) {
        try {
            return new String(toByteArray(this), charset);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public void writeInto(OutputStream out) {
        writeToStream(this, out);
    }
    
    private static void writeToStream(Stream<Chunk> in, final OutputStream out){
        try {
            final Lock lock = new Lock();
            
            in.scan(new Scanner<Representation.Chunk>() {
                @Override
                public void collect(Chunk next, boolean isLastChunk) {
                    next.writeInto(out);
                    if(isLastChunk){
                        lock.unlock();
                    }
                }
            });

            lock.waitForUnlock();
            out.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    private static byte[] toByteArray(Stream<Chunk> in){
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        writeToStream(in, out);
        return out.toByteArray();
    }
}
