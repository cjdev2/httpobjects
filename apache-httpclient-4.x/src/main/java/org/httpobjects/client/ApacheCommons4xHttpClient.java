package org.httpobjects.client;

import static java.util.Arrays.asList;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.httpobjects.Representation;
import org.httpobjects.Response;
import org.httpobjects.ResponseCode;
import org.httpobjects.header.GenericHeaderField;
import org.httpobjects.header.HeaderField;
import org.httpobjects.impl.fn.Fn;
import org.httpobjects.impl.fn.FunctionalJava;
import org.httpobjects.impl.fn.Seq;
import org.httpobjects.util.HttpObjectUtil;

public class ApacheCommons4xHttpClient implements HttpClient {
	private final org.apache.http.client.HttpClient client;
	
	public ApacheCommons4xHttpClient() {
		this(new DefaultHttpClient());
	}
	
	public ApacheCommons4xHttpClient(org.apache.http.client.HttpClient client) {
		super();
		this.client = client;
	}

	private final HttpResponse execute(HttpUriRequest request){
		try {
			return client.execute(request);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public RemoteObject resource(final String uri) {
		return new RemoteObject() {
			
			@Override
			public Response put(String query, Representation r, HeaderField ... fields) {
				return translate(execute(translate(query, r, fields, new HttpPut(uri))));
			}
			
			
			@Override
			public Response post(String query, Representation r, HeaderField ... fields) {
				return translate(execute(translate(query, r, fields, new HttpPost(uri))));
			}
			@Override
			public Response patch(String query, Representation r, HeaderField ... fields) {
				return translate(execute(translate(query, r, fields, new HttpPatch(uri))));
			}
			
			@Override
			public Response options(String query, Representation r, HeaderField ... fields) {
				return translate(execute(translate(query, r, fields, new HttpOptions(uri))));
			}
			
			@Override
			public Response head(String query, Representation r, HeaderField ... fields) {
				return translate(execute(translate(query, r, fields, new HttpHead(uri))));
			}
			
			
			@Override
			public Response get(String query, Representation r, HeaderField ... fields) {
				return translate(execute(translate(query, r, fields, new HttpGet(uri))));
			}
			
			
			@Override
			public Response delete(String query, Representation r, HeaderField ... fields) {
				return translate(execute(translate(query, r, fields, new HttpDelete(uri))));
			}
		};
	}
	
	/**
	 * This is missing in apache client 4.0
	 */
	private final class HttpPatch extends HttpEntityEnclosingRequestBase {
		private HttpPatch(final String uri) {
			setURI(URI.create(uri));
		}

		@Override
		public String getMethod() {
			return "PATCH";
		}
	}
	
	private Response translate(org.apache.http.HttpResponse apache) {
		final ResponseCode code = ResponseCode.forCode(apache.getStatusLine().getStatusCode());
		final Seq<HeaderField> headerFields = FunctionalJava.map(asList(apache.getAllHeaders()), new Fn<org.apache.http.Header, HeaderField>() {
			@Override
			public HeaderField exec(org.apache.http.Header in) {
				return new GenericHeaderField(in.getName(), in.getValue());
			}
		});
		
		final HttpEntity apacheBody = apache.getEntity();
		final Representation representation;
		if(apacheBody==null){
			representation = null;
		}else{
			representation = translate(apacheBody);
		}
		
		return new Response(code, representation, headerFields.toList().toArray(new HeaderField[]{}));
	}
	
	private Representation translate(final HttpEntity apache) {
		
		return new Representation() {
			
			@Override
			public void write(OutputStream out) {
				
				try {
					InputStream in = apache.getContent(); 
					try {
						copy(in, out);
					}finally{
						in.close();
					}
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
			
			private void copy(InputStream in, OutputStream out){
				try {
					byte[] buffer = new byte[1024 * 100];
					int x;
					while((x=in.read(buffer)) !=-1){
						out.write(buffer, 0, x);
					}
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
			
			@Override
			public String contentType() {
				org.apache.http.Header header = apache.getContentType();
				return header==null?null:header.getValue();
			}
		};
	}

	private org.apache.http.client.methods.HttpUriRequest translate(String query, Representation r, HeaderField[] fields, final HttpUriRequest in) {
		
		for(HeaderField field: fields){
			in.addHeader(field.name(), field.value());
		}
		
		if(r!=null){
			((HttpEntityEnclosingRequest)in).setEntity(translate(r));
		}
		
		return in;
	}

	private AbstractHttpEntity translate(final Representation representation) {
		final AbstractHttpEntity entity = new ByteArrayEntity(HttpObjectUtil.toByteArray(representation));
        
		entity.setContentType(representation.contentType());
		
		return entity;
	}

}
