package org.httpobjects.netty.http;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * An abstraction of a re-readable destination for bytes.
 */
public interface ByteAccumulator {
    
    /**
     * @return a new input stream for reading the contents of this accumulator
     */
    InputStream toStream();
    
    /**
     * @return the stream by which bytes can be added to this accumulator
     */
    OutputStream out();
    void dispose();
}