package org.httpobjects.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;

import org.httpobjects.Representation.Chunk;

public class ChunkImpl implements Chunk {
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
        public void writeInto(ByteBuffer buffer) {
            throw notImplemented();
        }
        
        @Override
        public void writeInto(WritableByteChannel out) {
            throw notImplemented();
        }
        
        @Override
        public void writeInto(OutputStream out) {
            try {
                out.write(buffer, offset, length);
            } catch (IOException e) {
                throw makeException(e);
            }
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
        
        protected RuntimeException makeException(Exception e){
            return new RuntimeException(e);
        }
        private RuntimeException notImplemented() {
            return new RuntimeException("NOT IMPLEMENTED");
        }
	}
//	@Override
//	public void write(OutputStream out) {
//		try {
//			byte[] buffer = new byte[1024];
//			for(int x=data.read(buffer);x!=-1;x=data.read(buffer)){
//				try{
//				    out.write(buffer, 0, x);
//				}catch(Exception err){
//				    throw new RuntimeException("Error writing representation.  This is probably because the connection to the remote host was closed.", err);
//				}
//			}
//			data.close();
//		} catch (IOException e) {
//			throw new RuntimeException(e);
//		}
//	}