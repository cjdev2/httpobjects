package org.httpobjects.netty;

import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.httpobjects.DSL;
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
import org.httpobjects.netty.http.HttpChannelHandler;
import org.httpobjects.path.Path;
import org.httpobjects.path.PathPattern;
import org.httpobjects.util.HttpObjectUtil;
import org.httpobjects.util.Method;
import org.jboss.netty.handler.codec.http.HttpChunkTrailer;
import org.jboss.netty.handler.codec.http.HttpRequest;

public class NettyHttpobjectsRequestHandler implements HttpChannelHandler.RequestHandler {
	private final List<HttpObject> objects;
    private final Response defaultResponse = DSL.NOT_FOUND();
    
	public NettyHttpobjectsRequestHandler(List<HttpObject> objects) {
		super();
		this.objects = objects;
	}
	
	@Override
	public Response respond(HttpRequest request, HttpChunkTrailer lastChunk, byte[] body) {
		
		final String uri = request.getUri();
		
		for(HttpObject next : objects){
		    final PathPattern pattern = next.pattern();
			if(pattern.matches(uri)){
				HttpObject match = null;
				match = next;
				Request in = readRequest(pattern, request, lastChunk, body);
				Method m = Method.fromString(request.getMethod().getName());
				Response out = HttpObjectUtil.invokeMethod(match, m, in);
				if(out!=null) return out;
			}
		}
		
        return defaultResponse;
	}
	
	private Request readRequest(final PathPattern pathPattern, final HttpRequest request, final HttpChunkTrailer lastChunk, final byte[] body) {
		return new Request(){
			
			@Override
			public boolean hasRepresentation() {
			    return body!=null;
			}

			@Override
			public RequestHeader header() {
				List<HeaderField> headers = new ArrayList<HeaderField>();
				for(String name: request.getHeaderNames()){
					for(String value: request.getHeaders(name)){
						final HeaderField field;
						if(name.equals("Cookie")){
							field = new CookieField(value);
						}else if(name.equals("Authorization")){
							field = AuthorizationField.parse(value);
						}else{
							field = new GenericHeaderField(name, value);
						}
						headers.add(field);
					}
				}
				return new RequestHeader(headers){
					@Override
					public AuthorizationField authorization() {
						final String value = request.getHeader("Authorization");
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
						return request.getHeader("ContentType");
					}
					
					@Override
					public void write(OutputStream out) {
						try {
							if(body!=null){
								out.write(body);
							}
						} catch (IOException e) {
							throw new RuntimeException(e);
						}
					}
				};
			}
			
		};
	}
	
	

}
