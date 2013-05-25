/**
 * Copyright (C) 2011, 2012 Commission Junction Inc.
 *
 * This file is part of httpobjects.
 *
 * httpobjects is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * httpobjects is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with httpobjects; see the file COPYING.  If not, write to the
 * Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA.
 *
 * Linking this library statically or dynamically with other modules is
 * making a combined work based on this library.  Thus, the terms and
 * conditions of the GNU General Public License cover the whole
 * combination.
 *
 * As a special exception, the copyright holders of this library give you
 * permission to link this library with independent modules to produce an
 * executable, regardless of the license terms of these independent
 * modules, and to copy and distribute the resulting executable under
 * terms of your choice, provided that you also meet, for each linked
 * independent module, the terms and conditions of the license of that
 * module.  An independent module is a module which is not derived from
 * or based on this library.  If you modify this library, you may extend
 * this exception to your version of the library, but you are not
 * obligated to do so.  If you do not wish to do so, delete this
 * exception statement from your version.
 */
package org.httpobjects.jetty;

import java.io.IOException;

import junit.framework.Assert;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.EntityEnclosingMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.httpobjects.HttpObject;
import org.httpobjects.Query;
import org.httpobjects.Request;
import org.httpobjects.Response;
import org.httpobjects.header.DefaultHeaderFieldVisitor;
import org.httpobjects.header.HeaderField;
import org.httpobjects.header.request.AuthorizationField;
import org.httpobjects.header.request.Cookie;
import org.httpobjects.header.request.CookieField;
import org.httpobjects.header.request.credentials.BasicCredentials;
import org.httpobjects.header.response.WWWAuthenticateField.Method;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public abstract class IntegrationTest {
	
	protected abstract void serve(int port, HttpObject ... objects);
	protected abstract void stopServing();
	
	@Before
	public void setup(){
		
		
		serve(8080,
				new HttpObject("/app/inbox"){
					public Response post(Request req) {
						return OK(Text("Message Received"));
					};
				},
				new HttpObject("/app/inbox/abc"){
					public Response put(Request req) {
						return OK(req.representation());
					};	
				},
				new HttpObject("/app"){
					public Response get(Request req) {
						return OK(Text("Welcome to the app"));
					};	
				},
				new HttpObject("/app/message"){
					public Response post(Request req) {
						return SEE_OTHER(Location("/app"), SetCookie("name", "frank"));
					};
				},
				new HttpObject("/nothing", null){},
				new HttpObject("/secure"){
					public Response get(Request req) {
						AuthorizationField authorization = req.header().authorization();
						if(authorization!=null && authorization.method()==Method.Basic){
	
							BasicCredentials creds = authorization.basicCredentials();
							if(creds.user().equals("Aladdin")&& creds.password().equals("open sesame")){
								return OK(Text("You're In!"));
							}
						}
						return UNAUTHORIZED(BasicAuthentication("secure area"), Text("You must first log-in"));
					};
				},
				new HttpObject("/echoUrl/{id}/{name}"){
				    @Override
				    public Response get(Request req) {
				        try {
    				        final String mode = req.query().valueFor("mode");
    				        if(mode == null){
    				            final String query = req.query().toString();
    				            final String tail = query.isEmpty()?"":("?" + query);
    				            return OK(Text(req.path().toString() + tail));
    				        }else if(mode.equals("printParams")){
    				            final StringBuffer text = new StringBuffer();
    				            final Query query = req.query();
    				            for(String name : query.paramNames()){
    				                if(text.length()>0){
    				                    text.append('\n');
    				                }
    				                text.append(name + "=" + query.valueFor(name));
    				            }
    				           return OK(Text(text.toString()));
    				        }else{
    				            return BAD_REQUEST();
    				        }
                        } catch (Exception e) {
                            e.printStackTrace();
                            return INTERNAL_SERVER_ERROR(e);
                        }
				    }
				},
				new HttpObject("/echoCookies"){
					public Response get(Request req) {
						
						final StringBuffer text = new StringBuffer();
						for(HeaderField next : req.header().fields()){
							next.accept(new DefaultHeaderFieldVisitor<Void>(){
								@Override
								public Void visit(CookieField cookieField) {
									for(Cookie cookie : cookieField.cookies()){
										text.append(cookie.name + "=" + cookie.value);
									}
									return null;
								}
							});
						}
						
						return OK(Text(text.toString()));
					};
				}
		);
	}
	
	@Test
	public void requestCookiesAreTranslated(){
		// WHEN
		GetMethod get = new GetMethod("http://localhost:8080/echoCookies");
		get.setRequestHeader("Cookie", "Larry=Moe");
		
		assertResource(get, "Larry=Moe", 200);
		
	}
	
	@Test
	public void basicAuthentication(){
		// without authorization header
		assertResource(new GetMethod("http://localhost:8080/secure"), "You must first log-in", 401, new HeaderSpec("WWW-Authenticate", "Basic realm=secure area"));
		
		// with authorization header
		GetMethod get = new GetMethod("http://localhost:8080/secure");
		get.setRequestHeader("Authorization", "Basic QWxhZGRpbjpvcGVuIHNlc2FtZQ==");
		assertResource(get, "You're In!", 200);
	}
	
	@Test
	public void nullResponsesAreTreatedAsNotFound(){
		assertResource(new GetMethod("http://localhost:8080/nothing"), 404);
	}
	
	@Test
	public void returnsNotFoundIfThereIsNoMatchingPattern(){
		assertResource(new GetMethod("http://localhost:8080/bob"), 404);
	}
	
	@Test
	public void happyPathForGet(){
		assertResource(new GetMethod("http://localhost:8080/app"), "Welcome to the app", 200);
	}
	
	@Test
	public void happyPathForPost(){
		assertResource(new PostMethod("http://localhost:8080/app/inbox"), "Message Received", 200);
	}
	
	@Test
	public void happyPathForPut(){
		assertResource(withBody(new PutMethod("http://localhost:8080/app/inbox/abc"), "hello world"), "hello world", 200);
	}
	
	@Test
    public void queryParameters(){
        assertResource(withBody(new PutMethod("http://localhost:8080/app/inbox/abc"), "hello world"), "hello world", 200);
    }
    
	@Test
	public void urlToString(){
	    assertResource(new GetMethod("http://localhost:8080/echoUrl/34/marty?a=1&b=2"), "/echoUrl/34/marty?a=1&b=2", 200);
        assertResource(new GetMethod("http://localhost:8080/echoUrl/44/foo"), "/echoUrl/44/foo", 200);
	}
	
	@Test
	public void methodNotAllowed(){
		assertResource(new GetMethod("http://localhost:8080/app/inbox"), "405 Client Error: Method Not Allowed", 405);
	}
	
	@Test
	public void redirectsAndSetsCookies(){

		assertResource(new PostMethod("http://localhost:8080/app/message"), 303, 
				new HeaderSpec("Location", "/app"),
				new HeaderSpec("Set-Cookie", "name=frank"));
	}
	
	@SuppressWarnings("deprecation")
	private static <T extends EntityEnclosingMethod> T withBody(T m, String body){
		m.setRequestBody(body);
		return m;
	}

	private void assertResource(HttpMethod method,
			int expectedResponseCode, HeaderSpec ... header) {
		assertResource(method, null, expectedResponseCode, header);
	}
	private void assertResource(HttpMethod method, String expectedBody,
			int expectedResponseCode, HeaderSpec ... header) {
		try {
			HttpClient client = new HttpClient();
			int response = client.executeMethod(method);

			Assert.assertEquals(expectedResponseCode, response);
			if(expectedBody!=null) Assert.assertEquals(expectedBody, method.getResponseBodyAsString());
			
			if(header!=null){
				for(HeaderSpec next : header){
					Header h = method.getResponseHeader(next.name);
					Assert.assertNotNull("Expected a \"" + next.name + "\" value of \"" + next.value + "\"", h);
					Assert.assertEquals(next.value, h.getValue());
				}
			}
		} catch (HttpException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private class HeaderSpec {
		final String name;
		final String value;
		private HeaderSpec(String name, String value) {
			super();
			this.name = name;
			this.value = value;
		}
		
	}
	
	@After
	public void tearDown() throws Exception {
		stopServing();
	}
}
