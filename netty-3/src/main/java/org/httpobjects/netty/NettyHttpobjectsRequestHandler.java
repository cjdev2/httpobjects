package org.httpobjects.netty;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.httpobjects.ConnectionInfo;
import org.httpobjects.DSL;
import org.httpobjects.Eventual;
import org.httpobjects.HttpObject;
import org.httpobjects.Query;
import org.httpobjects.Representation;
import org.httpobjects.Request;
import org.httpobjects.Response;
import org.httpobjects.header.GenericHeaderField;
import org.httpobjects.header.HeaderField;
import org.httpobjects.header.request.AuthorizationField;
import org.httpobjects.header.request.CookieField;
import org.httpobjects.header.request.RequestHeader;
import org.httpobjects.impl.ByteStreamImpl;
import org.httpobjects.impl.ChunkImpl;
import org.httpobjects.netty.http.ByteAccumulator;
import org.httpobjects.netty.http.HttpChannelHandler;
import org.httpobjects.path.Path;
import org.httpobjects.path.PathPattern;
import org.httpobjects.util.HttpObjectUtil;
import org.httpobjects.util.Method;
import org.jboss.netty.handler.codec.http.HttpChunkTrailer;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpRequest;

public class NettyHttpobjectsRequestHandler implements HttpChannelHandler.RequestHandler {
	private final List<HttpObject> objects;
    private final Eventual<Response> defaultResponse = DSL.NOT_FOUND();

	public NettyHttpobjectsRequestHandler(List<HttpObject> objects) {
		super();
		this.objects = objects;
	}
	
	@Override
	public Eventual<Response> respond(HttpRequest request, HttpChunkTrailer lastChunk, ByteAccumulator body, ConnectionInfo connectionInfo) {
		
		final String uri = request.getUri();
		
		for(HttpObject next : objects){
		    final PathPattern pattern = next.pattern();
			if(pattern.matches(uri)){
				HttpObject match = null;
				match = next;
				Request in = readRequest(pattern, request, lastChunk, body, connectionInfo);
				Method m = Method.fromString(request.getMethod().getName());
				Eventual<Response> out = HttpObjectUtil.invokeMethod(match, m, in);
				if(out!=null) return out;
			}
		}
		
        return defaultResponse;
	}
	
	private Request readRequest(final PathPattern pathPattern, final HttpRequest request, final HttpChunkTrailer lastChunk, final ByteAccumulator body, final ConnectionInfo connectionInfo) {
		return new Request(){
			
			@Override
			public boolean hasRepresentation() {
			    return body!=null;
			}
			
			@Override
			public ConnectionInfo connectionInfo() {
			    return connectionInfo;
			}
			
		    @Override
		    public Request immutableCopy() {
		        return this;
		    }
			
			@Override
			public RequestHeader header() {
				List<HeaderField> results = new ArrayList<HeaderField>();
				final HttpHeaders headers = request.headers(); 
				for(String name: headers.names()){
					for(String value: headers.getAll(name)){
						final HeaderField field;
						if(name.equals("Cookie")){
							field = new CookieField(value);
						}else if(name.equals("Authorization")){
							field = AuthorizationField.parse(value);
						}else{
							field = new GenericHeaderField(name, value);
						}
						results.add(field);
					}
				}
				return new RequestHeader(results){
					@Override
					public AuthorizationField authorization() {
						final String value = headers.get("Authorization");
						return value==null?null:AuthorizationField.parse(value);
					}
				};
			}
			
			@Override
			public Path path() {
			    return pathPattern.match(jdkURL().getPath());
			}
			
			private URL jdkURL(){
                try {
                    return new URL("http://foo" + request.getUri());
                } catch (MalformedURLException e) {
                    throw new RuntimeException(e);
                }
			    
			}
			
			@Override
			public Query query() {
			    return new Query(jdkURL().getQuery());
			}
			
			@Override
			public Representation representation() {
				
				return new Representation(){
					@Override
					public String contentType() {
						return request.headers().get("ContentType");
					}
					
		            @Override
		            public ByteStream bytes() {
		                return new ByteStreamImpl(){
		                    @Override
		                    public void scan(org.httpobjects.Stream.Scanner<Chunk> scanner) {
		                        try {
		                            if(body != null){
	                                    final InputStream data = body.toStream();
		                                final byte[] buffer = new byte[1024];
		                                while(true) {
		                                    final int x = data.read(buffer);
		                                    if(x==-1) {
		                                        scanner.collect(ChunkImpl.NULL_CHUNK, true);
		                                        break;
		                                    }else{
		                                        scanner.collect(new ChunkImpl(buffer, 0, x), false);
		                                    }
		                                }
		                                data.close();
		                            }
		                        } catch (IOException e) {
		                            throw new RuntimeException(e);
		                        }
		                    }
		                };
		            }
				};
			}
			
		};
	}

}
