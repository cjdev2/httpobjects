package org.httpobjects.netty.http;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class FilesystemByteAccumulatorFactory implements ByteAccumulatorFactory {
    private final File tempDir;
    private final String prefix = getClass().getSimpleName();
    
    public FilesystemByteAccumulatorFactory(File tempDir) {
        this.tempDir = tempDir;
    }

    @Override
    public ByteAccumulator newAccumulator() {
        try {
            final File path = File.createTempFile(prefix, ".body", tempDir);
            return new ByteAccumulator() {
                
                @Override
                public InputStream toStream() {
                    try {
                        return new FileInputStream(path);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
                
                @Override
                public OutputStream out() {
                    try {
                        return new FileOutputStream(path);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
                @Override
                public void dispose() {
                    if(!path.delete()) throw new RuntimeException("Unable to delete " + path.getAbsolutePath());
                }
            };
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
