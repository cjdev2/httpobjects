package org.httpobjects.netty;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.httpobjects.*;
import org.httpobjects.header.GenericHeaderField;
import org.httpobjects.header.HeaderField;
import org.httpobjects.header.request.AuthorizationField;
import org.httpobjects.header.request.CookieField;
import org.httpobjects.header.request.RequestHeader;
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
			public Request immutableCopy() {
				return this;
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
					public void write(OutputStream out) {
						try {
							if(body!=null){
							    InputStream data = body.toStream();
					            copy(out, data);
							}
						} catch (IOException e) {
							throw new RuntimeException(e);
						}
					}

                    private void copy(OutputStream out, InputStream data) throws IOException {
                        byte[] buffer = new byte[1024 * 10];
                        int x;
                        while((x = data.read(buffer))!=-1){
                            out.write(buffer, 0, x);
                        }
                        out.close();
                        data.close();
                    }
				};
			}
			
		};
	}
	
	

}
