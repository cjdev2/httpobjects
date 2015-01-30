package org.httpobjects.netty.http;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class InMemoryByteAccumulatorFactory implements ByteAccumulatorFactory {
    @Override
    public ByteAccumulator newAccumulator() {
        return new ByteAccumulator(){
            private final ByteArrayOutputStream buf = new ByteArrayOutputStream();
            
            @Override
            public OutputStream out() {
                return buf;
            }
            @Override
            public InputStream toStream() {
                return new ByteArrayInputStream(buf.toByteArray());
            }
            @Override
            public void dispose() {
            }
        };
    }
}
