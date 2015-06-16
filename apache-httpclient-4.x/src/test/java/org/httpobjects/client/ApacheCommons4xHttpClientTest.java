package org.httpobjects.client;
import static org.junit.Assert.assertEquals;

import org.httpobjects.DSL;
import org.httpobjects.HttpObject;
import org.httpobjects.Representation;
import org.httpobjects.Request;
import org.httpobjects.Response;
import org.httpobjects.ResponseCode;
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
								Method.POST, 
								"http://localhost:" + port + "/echo",
								DSL.Text("this is my content\nsee it?"),
								new GenericHeaderField("echo-header-A", "alpha"),
								new GenericHeaderField("echo-header-B", "beta"));
			
			// when
			final Response response = testSubject.send(request);
			
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
			final ClientRequest request = new ClientRequest(Method.GET, "http://localhost:" + port + "/some/resource/with/headers");
			
			// when
			final Response response = testSubject.send(request);
			
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
	public void supportsAllTheMethods(){
		// given
		final Channel server = HttpobjectsNettySupport.serve(port, new HttpObject("/i-have-all-the-methods"){
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
				});
		try{
			for(Method method : Method.values()){

				final HttpClient testSubject = new ApacheCommons4xHttpClient();
				final ClientRequest request = new ClientRequest(method, "http://localhost:" + port + "/i-have-all-the-methods");
				
				// when
				final Response response = testSubject.send(request);
				
				// then
				assertEquals(ResponseCode.OK, response.code());
				assertEquals(method.name().toLowerCase(), findByName("method-name", response.header()).value());
			}
		}finally{
			server.close();
		}
		
	}
	
	
	
	private HeaderField findByName(String name, HeaderField[] fields){
		for(HeaderField field: fields){
			if(field.name().equals(name)) return field;
		}
		return null;
	}
}
