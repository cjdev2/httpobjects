package org.httpobjects.netty;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.httpobjects.DSL;
import org.httpobjects.HttpObject;
import org.httpobjects.Representation;
import org.httpobjects.Request;
import org.httpobjects.Response;
import org.httpobjects.header.HeaderField;
import org.httpobjects.header.OtherHeaderField;
import org.httpobjects.header.request.AuthorizationField;
import org.httpobjects.header.request.CookieField;
import org.httpobjects.header.request.RequestHeader;
import org.httpobjects.header.response.SetCookieField;
import org.httpobjects.header.response.WWWAuthenticateField;
import org.httpobjects.netty.http.HttpChannelHandler;
import org.httpobjects.netty.http.HttpChannelHandler.RequestHandler;
import org.httpobjects.path.PathVariables;
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
	public Response respond(HttpRequest request, HttpChunkTrailer lastChunk,
			byte[] body) {
		
		final String uri = request.getUri();
		
		for(HttpObject next : objects){
			System.out.println("comparing " + next.pattern().raw() + " and " + uri);
			if(next.pattern().matches(uri)){
				HttpObject match = null;
				match = next;
				Request in = readRequest(request, lastChunk, body);
				Method m = Method.fromString(request.getMethod().getName());
				Response out = HttpObjectUtil.invokeMethod(match, m, in);
				if(out!=null) return out;
			}
		}
		
			return defaultResponse;
	}
	
	private Request readRequest(final HttpRequest request, final HttpChunkTrailer lastChunk, final byte[] body) {
		return new Request(){
			@Override
			public List<SetCookieField> cookies() {
				throw notImplemented();
			}
			
			@Override
			public String getParameter(String string) {
				throw notImplemented();
			}
			
			@Override
			public boolean hasRepresentation() {
				throw notImplemented();
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
							field = new OtherHeaderField(name, value);
						}
						headers.add(field);
					}
				}
				return new RequestHeader(headers){
					@Override
					public AuthorizationField authorization() {
						final boolean yesOrNo = lastChunk!=null && lastChunk.containsHeader("Authorization");
						System.out.println("Header fields are " + request.getHeaderNames());
						String value = request.getHeader("Authorization");
						return value==null?null:AuthorizationField.parse(value);
					}
				};
			}
			
			@Override
			public Request immutableCopy() {
				throw notImplemented();
			}
			
			@Override
			public PathVariables pathVars() {
				throw notImplemented();
			}
			
			@Override
			public String query() {
				throw notImplemented();
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
			
			private RuntimeException notImplemented(){
				return new RuntimeException("not implemented");
			}
		};
	}
	
	

}
