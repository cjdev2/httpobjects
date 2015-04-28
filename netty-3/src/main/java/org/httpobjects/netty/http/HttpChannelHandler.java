/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package org.httpobjects.netty.http;

import static org.jboss.netty.handler.codec.http.HttpHeaders.is100ContinueExpected;
import static org.jboss.netty.handler.codec.http.HttpHeaders.isKeepAlive;
import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.CONNECTION;
import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH;
import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.COOKIE;
import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.SET_COOKIE;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.CONTINUE;
import static org.jboss.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Set;

import org.httpobjects.ConnectionInfo;
import org.httpobjects.Response;
import org.httpobjects.header.HeaderField;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.http.Cookie;
import org.jboss.netty.handler.codec.http.CookieDecoder;
import org.jboss.netty.handler.codec.http.CookieEncoder;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpChunk;
import org.jboss.netty.handler.codec.http.HttpChunkTrailer;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;

public class HttpChannelHandler extends SimpleChannelUpstreamHandler {
	
	public static interface RequestHandler {
		Response respond(HttpRequest request, HttpChunkTrailer lastChunk, ByteAccumulator body, ConnectionInfo connection);
	}
	
	private final RequestHandler handler;
	private final ByteAccumulator contentAccumulator;
    private HttpRequest request;
    private boolean readingChunks;
    
    public HttpChannelHandler(RequestHandler handler, ByteAccumulator contentAccumulator) {
		this.handler = handler;
		this.contentAccumulator = contentAccumulator;
	}

	@Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        if (!readingChunks) {
            HttpRequest request = this.request = (HttpRequest) e.getMessage();

            if (is100ContinueExpected(request)) {
                send100Continue(e);
            }

            if (request.isChunked()) {
                readingChunks = true;
            } else {
                ChannelBuffer content = request.getContent();
                if (content.readable()) {
                	writeToBuffer(content);
                }
                
            	writeResponse(e.getChannel(), handler.respond(request, null, contentAccumulator, connectionInfo(ctx)));
            }
        } else {
            HttpChunk chunk = (HttpChunk) e.getMessage();
            if (chunk.isLast()) {
                readingChunks = false;

                HttpChunkTrailer trailer = (HttpChunkTrailer) chunk;
                writeToBuffer(trailer.getContent());
            	writeResponse(e.getChannel(), handler.respond(request, trailer, contentAccumulator, connectionInfo(ctx)));
            } else {
            	writeToBuffer(chunk.getContent());
            }
        }
    }
    
	@SuppressWarnings("unchecked")
    private <O, T extends O> T cast(O o){return (T)o;}
	
    private ConnectionInfo connectionInfo(ChannelHandlerContext ctx) {
        final Channel channel = ctx.getChannel();
        final InetSocketAddress local = cast(channel.getLocalAddress());
        final InetSocketAddress remote = cast(channel.getRemoteAddress());
        return new ConnectionInfo(
                        local.getAddress().getHostAddress(),
                        local.getPort(),
                        remote.getAddress().getHostAddress(),
                        remote.getPort());
    }
    
    private void writeToBuffer(ChannelBuffer content) throws IOException {
    	content.getBytes(0, contentAccumulator.out(), content.capacity());
    }
    
    private byte[] read(Response out) {
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            out.representation().write(stream);
            stream.close();
            return stream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private void writeResponse(/*MessageEvent e*/ Channel sink, Response r) {
    	
        // Decide whether to close the connection or not.
        boolean keepAlive = isKeepAlive(request);

        // Build the response object.
        HttpResponseStatus status = HttpResponseStatus.valueOf(r.code().value());
        
        HttpResponse response = new DefaultHttpResponse(HTTP_1_1, status);
        if(r.hasRepresentation()){
        	response.setContent(ChannelBuffers.copiedBuffer(read(r)));
        	if(r.representation().contentType() != null)
        		response.headers().set(CONTENT_TYPE, r.representation().contentType());
        }
        
        for(HeaderField field : r.header()){
            response.headers().add(field.name(), field.value());
        }

        if (keepAlive) {
            // Add 'Content-Length' header only for a keep-alive connection.
            response.headers().set(CONTENT_LENGTH, response.getContent().readableBytes());
            // Add keep alive header as per:
            // - http://www.w3.org/Protocols/HTTP/1.1/draft-ietf-http-v11-spec-01.html#Connection
            response.headers().set(CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
        }

        // Encode the cookie.
        String cookieString = request.headers().get(COOKIE);
        if (cookieString != null) {
            CookieDecoder cookieDecoder = new CookieDecoder();
            Set<Cookie> cookies = cookieDecoder.decode(cookieString);
            if (!cookies.isEmpty()) {
                // Reset the cookies if necessary.
                CookieEncoder cookieEncoder = new CookieEncoder(true);
                for (Cookie cookie : cookies) {
                    cookieEncoder.addCookie(cookie);
                    response.headers().add(SET_COOKIE, cookieEncoder.encode());
                }
            }
        }

        // Write the response.
          ChannelFuture future = sink.write(response);

        // Close the non-keep-alive connection after the write operation is done.
        if (!keepAlive) {
            future.addListener(ChannelFutureListener.CLOSE);
        }
    }

    private static void send100Continue(MessageEvent e) {
        HttpResponse response = new DefaultHttpResponse(HTTP_1_1, CONTINUE);
        e.getChannel().write(response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
        e.getCause().printStackTrace();
        e.getChannel().close();
    }
}