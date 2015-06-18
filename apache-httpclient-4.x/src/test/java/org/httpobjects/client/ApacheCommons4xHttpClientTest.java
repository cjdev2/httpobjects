package org.httpobjects.client;
import static org.junit.Assert.assertEquals;

import org.httpobjects.DSL;
import org.httpobjects.HttpObject;
import org.httpobjects.Representation;
import org.httpobjects.Request;
import org.httpobjects.Response;
import org.httpobjects.ResponseCode;
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
	public void sendsRequests(){
		// given
		final Channel server = HttpobjectsNettySupport.serve(port, 
				new HttpObject("/echo"){
					@Override
					public Response post(Request req) {
						final StringBuffer text = new StringBuffer(req.path().toString() + req.query().toString());
						for(HeaderField field : req.header().fields()){
							if(field.name().startsWith("echo-")){
								text.append("\n" + field.name() + "=" + field.value());
							}
						}
						final Representation r = req.representation();
						if(r!=null){
							text.append("\n" + HttpObjectUtil.toAscii(r));
						}
						return OK(Text(text.toString()));
					}
				});
		try{

			final HttpClient testSubject = new ApacheCommons4xHttpClient();
			final ClientRequest request = new ClientRequest(
								DSL.Text("this is my content\nsee it?"),
								new GenericHeaderField("echo-header-A", "alpha"),
								new GenericHeaderField("echo-header-B", "beta"));
			
			// when
			final Response response = testSubject
										.resource("http://localhost:" + port + "/echo")
										.post(request);
			// then
			assertEquals(
					     "/echo\n" +
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
	public void returnsResponses(){
		// given
		final Channel server = HttpobjectsNettySupport.serve(port, 
				new HttpObject("/some/resource/with/headers"){
					@Override
					public Response get(Request req) {
						return OK(Text("You GET it"), new GenericHeaderField("a-custom-header-name", "a-custom-header-value"));
					}
				});
		try{

			final HttpClient testSubject = new ApacheCommons4xHttpClient();
			
			// when
			final Response response = testSubject
										.resource("http://localhost:" + port + "/some/resource/with/headers")
										.get(new ClientRequest());
			
			// then
			assertEquals(ResponseCode.OK, response.code());
			assertEquals("You GET it", HttpObjectAssert.bodyOf(response).asString());
			HttpObjectAssert.contentTypeOf(response).assertIs("text/plain; charset=utf-8");
			
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
		final Channel server = HttpobjectsNettySupport.serve(port, new MethodEchoer("/i-have-all-the-methods"));
		try{
			for(Method method : Method.values()){

				final HttpClient testSubject = new ApacheCommons4xHttpClient();
				final ClientRequest request = new ClientRequest();
				
				final java.lang.reflect.Method m = RemoteObject.class.getMethod(method.name().toLowerCase(), ClientRequest.class);
				final RemoteObject o = testSubject.resource("http://localhost:" + port + "/i-have-all-the-methods");
				
				// when
				final Response response = (Response) m.invoke(o, request);
				
				// then
				assertEquals(ResponseCode.OK, response.code());
				assertEquals(method.name().toLowerCase(), findByName("method-name", response.header()).value());
			}
		}finally{
			server.close();
		}
		
	}
	
	@Test
	public void supportsAllTheMethodsWithTheNoArgsConvenienceVersion() throws Exception{
		// given
		final Channel server = HttpobjectsNettySupport.serve(port, new MethodEchoer("/i-have-all-the-methods"));
		try{
			for(Method method : Method.values()){

				final HttpClient testSubject = new ApacheCommons4xHttpClient();
				
				final java.lang.reflect.Method m = RemoteObject.class.getMethod(method.name().toLowerCase());
				final RemoteObject o = testSubject.resource("http://localhost:" + port + "/i-have-all-the-methods");
				
				// when
				final Response response = (Response) m.invoke(o);
				
				// then
				assertEquals(ResponseCode.OK, response.code());
				assertEquals(method.name().toLowerCase(), findByName("method-name", response.header()).value());
			}
		}finally{
			server.close();
		}
		
	}
	
	private static class MethodEchoer extends HttpObject {
		public MethodEchoer(String pattern) {
			super(pattern);
		}
		private Response make(String name){
			return OK(Text(name), new GenericHeaderField("method-name", name));
		}
	    @Override public Response delete(Request req){return make("delete");}
	    @Override public Response get(Request req){return make("get");}
	    @Override public Response head(Request req){return make("head");}
	    @Override public Response options(Request req){return make("options");}
	    @Override public Response post(Request req){return make("post");}
	    @Override public Response put(Request req){return make("put");}
	    @Override public Response trace(Request req){return make("trace");}
	    @Override public Response patch(Request req){return make("patch");}
	}
	
	private HeaderField findByName(String name, HeaderField[] fields){
		for(HeaderField field: fields){
			if(field.name().equals(name)) return field;
		}
		return null;
	}
}
