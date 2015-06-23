package org.httpobjects.client;

import static org.httpobjects.DSL.Text;
import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import org.httpobjects.*;
import org.httpobjects.client.HttpClient.RemoteObject;
import org.httpobjects.header.GenericHeaderField;
import org.httpobjects.header.HeaderField;
import org.httpobjects.netty.HttpobjectsNettySupport;
import org.httpobjects.test.HttpObjectAssert;
import org.httpobjects.util.HttpObjectUtil;
import org.httpobjects.util.Method;
import org.jboss.netty.channel.Channel;
import org.junit.Test;

public class ApacheCommons4xHttpClientTest {
	final int port = 8080;

	@Test
	public void sendsRequests() throws Exception{
		// given
		final Channel server = serve(port, new Echoer("/echo"));
		try{

			final HttpClient testSubject = new ApacheCommons4xHttpClient();
			
			// when
			final Response response = testSubject
										.resource("http://localhost:" + port + "/echo")
										.post(DSL.Text("this is my content\nsee it?"),
												new GenericHeaderField("echo-header-A", "alpha"),
												new GenericHeaderField("echo-header-B", "beta"));
			// then
			assertEquals(
					    "POST /echo\n" +
						"echo-header-A=alpha\n" +
						"echo-header-B=beta\n" +
						"this is my content\n" +
						"see it?", 
						HttpObjectAssert.bodyOf(response).asString());
		}finally{
			server.close();
		}
	}
	
	@Test
	public void returnsResponses() throws Exception{
		// given
		final Channel server = serve(port, 
				new HttpObject("/some/resource/with/headers"){
					@Override
					public Eventual<Response> get(Request req) {
						return OK(Text("You GET it"), new GenericHeaderField("a-custom-header-name", "a-custom-header-value")).toFuture();
					}
				});
		try{

			final HttpClient testSubject = new ApacheCommons4xHttpClient();
			
			// when
			final Response response = testSubject
										.resource("http://localhost:" + port + "/some/resource/with/headers")
										.get();
			
			// then
			assertEquals(ResponseCode.OK, response.code());
			assertEquals("You GET it", HttpObjectAssert.bodyOf(response).asString());
			assertEquals("text/plain; charset=utf-8", response.representation().contentType().toLowerCase());
			
			assertEquals(
					"a-custom-header-value", 
					findByName("a-custom-header-name", response.header()).value());
		}finally{
			server.close();
		}
	}
	
	@Test
	public void supportsAllTheMethods() throws Exception{
		// given
		final Channel server = serve(port, new MethodEchoer("/i-have-all-the-methods"));
		try{
			for(Method method : Method.values()){

				final HttpClient testSubject = new ApacheCommons4xHttpClient();
				
				final java.lang.reflect.Method m = RemoteObject.class.getMethod(method.name().toLowerCase(), String.class, HeaderField[].class);
				final RemoteObject o = testSubject.resource("http://localhost:" + port + "/i-have-all-the-methods");
				
				// when
				final Response response = (Response) m.invoke(o, "?foo=bar", (Object) new HeaderField[]{new GenericHeaderField("echo-foo", "bar")});
				
				// then
				assertEquals(ResponseCode.OK, response.code());
				assertEquals(method.name().toLowerCase(), findByName("method-name", response.header()).value());
			}
		}finally{
			server.close();
		}
		
	}
	@Test
	public void supportsAllTheMethodsWithTheQueryOnlyConvenienceVersion() throws Exception{
		// given
		final Channel server = serve(port, new Echoer("/i-have-all-the-methods"));
		try{
			for(Method method : Method.values()){

				final HttpClient testSubject = new ApacheCommons4xHttpClient();
				
				final java.lang.reflect.Method m = RemoteObject.class.getMethod(method.name().toLowerCase(), String.class, HeaderField[].class);
				final RemoteObject o = testSubject.resource("http://localhost:" + port + "/i-have-all-the-methods");
				
				// when
				final Response response = (Response) m.invoke(o, "?foo=bar", (Object) new HeaderField[]{new GenericHeaderField("echo-foo", "bar")});
				
				// then
				assertEquals(ResponseCode.OK, response.code());
				assertEquals(method.name().toUpperCase() + " /i-have-all-the-methods?foo=bar\necho-foo=bar\n", bodyInHeader(response));
			}
		}finally{
			server.close();
		}
	}
	@Test
	public void supportsAllTheMethodsWithTheRepresentationOnlyConvenienceVersion() throws Exception{
		// given
		final Channel server = serve(port, new Echoer("/i-have-all-the-methods"));
		try{
			for(Method method : Method.values()){

				final HttpClient testSubject = new ApacheCommons4xHttpClient();
				
				final java.lang.reflect.Method m = RemoteObject.class.getMethod(method.name().toLowerCase(), Representation.class, HeaderField[].class);
				final RemoteObject o = testSubject.resource("http://localhost:" + port + "/i-have-all-the-methods");
				
				// when
				final Response response = (Response) m.invoke(o, Text("yo"), (Object) new HeaderField[]{new GenericHeaderField("echo-foo", "bar")});
				
				// then
				assertEquals(ResponseCode.OK, response.code());
				assertEquals(method.name().toUpperCase() + " /i-have-all-the-methods\necho-foo=bar\nyo", bodyInHeader(response));
			}
		}finally{
			server.close();
		}
	}
	
	@Test
	public void supportsAllTheMethodsWithTheNoArgsConvenienceVersion() throws Exception{
		// given
		final Channel server = serve(port, new Echoer("/i-have-all-the-methods"));
		try{
			for(Method method : Method.values()){

				final HttpClient testSubject = new ApacheCommons4xHttpClient();
				
				final java.lang.reflect.Method m = RemoteObject.class.getMethod(method.name().toLowerCase(), HeaderField[].class);
				final RemoteObject o = testSubject.resource("http://localhost:" + port + "/i-have-all-the-methods");
				
				// when
				final Response response = (Response) m.invoke(o, (Object) new HeaderField[]{new GenericHeaderField("echo-foo", "bar")});
				
				// then
				assertEquals(ResponseCode.OK, response.code());
				assertEquals(method.name().toUpperCase() + " /i-have-all-the-methods\necho-foo=bar\n", bodyInHeader(response));
			}
		}catch(Exception e){
			throw new RuntimeException(e);
		}
		finally{
			server.close();
		}
		
	}
	
	private Channel serve(int port, HttpObject ... objects) {
		return HttpobjectsNettySupport.serve(DSL.syncronousExecutor(), port, objects);
	}

	private String bodyInHeader(Response response){
		try {
			return URLDecoder.decode(findByName("body", response.header()).value(), "UTF8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}
	
	private static class MethodEchoer extends HttpObject {
		public MethodEchoer(String pattern) {
			super(pattern);
		}
		private Eventual<Response> make(String name){
			return OK(Text(name), new GenericHeaderField("method-name", name)).toFuture();
		}
	    @Override public Eventual<Response> delete(Request req){return make("delete");}
	    @Override public Eventual<Response> get(Request req){return make("get");}
	    @Override public Eventual<Response> head(Request req){return make("head");}
	    @Override public Eventual<Response> options(Request req){return make("options");}
	    @Override public Eventual<Response> post(Request req){return make("post");}
	    @Override public Eventual<Response> put(Request req){return make("put");}
	    @Override public Eventual<Response> trace(Request req){return make("trace");}
	    @Override public Eventual<Response> patch(Request req){return make("patch");}
	}

	private static class Echoer extends HttpObject {

		public Echoer(String pathPattern) {
			super(pathPattern);
		}

	    @Override public Eventual<Response> delete(Request req){return make("delete", req);}
	    @Override public Eventual<Response> get(Request req){return make("get", req);}
	    @Override public Eventual<Response> head(Request req){return make("head", req);}
	    @Override public Eventual<Response> options(Request req){return make("options", req);}
	    @Override public Eventual<Response> post(Request req){return make("post", req);}
	    @Override public Eventual<Response> put(Request req){return make("put", req);}
	    @Override public Eventual<Response> trace(Request req){return make("trace", req);}
	    @Override public Eventual<Response> patch(Request req){return make("patch", req);}

		private Eventual<Response> make(final String method, final Request req) {
			try {
				final StringBuffer text = new StringBuffer(method.toUpperCase() + " " + req.path().toString() + req.query().toString());
				for(HeaderField field : req.header().fields()){
					if(field.name().startsWith("echo-")){
						text.append("\n" + field.name() + "=" + field.value());
					}
				}
				final Representation r = req.representation();
				if(r!=null){
					text.append("\n" + HttpObjectUtil.toAscii(r));
				}
				return OK(Text(text.toString()), new GenericHeaderField("body", URLEncoder.encode(text.toString(), "UTF8"))).toFuture();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	private HeaderField findByName(String name, HeaderField[] fields){
		for(HeaderField field: fields){
			if(field.name().equals(name)) return field;
		}
		return null;
	}
}
