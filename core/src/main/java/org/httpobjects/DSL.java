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
package org.httpobjects;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;

import org.httpobjects.header.HeaderField;
import org.httpobjects.header.response.AllowField;
import org.httpobjects.header.response.LocationField;
import org.httpobjects.header.response.SetCookieField;
import org.httpobjects.header.response.WWWAuthenticateField;
import org.httpobjects.representation.BinaryRepresentation;
import org.httpobjects.util.ClasspathResourcesObject;
import org.httpobjects.util.Method;
import org.httpobjects.util.impl.ClassResourceLoader;
import org.httpobjects.util.impl.ResourceLoader;
import org.httpobjects.util.impl.WrapperForInsecureClassloader;

/**########################################################
 * ## DSL METHODS
 * ########################################################
 */
public class DSL {
    //according to
    //http://w3techs.com/technologies/overview/character_encoding/all
    //pulled on 2013-07-18
    //UTF-8 is used on 76.0% of all websites
    //ISO-8859-1 is used on 12.0% of all websites
    public static final StandardCharset MOST_WIDELY_SUPPORTED_ENCODING = StandardCharset.UTF_8;
    public static final StandardCharset DEFAULT_HTTP_ENCODING = StandardCharset.ISO_8859_1;
    
    public static final String CONTENT_TYPE_CSV = "text/csv; charset="+MOST_WIDELY_SUPPORTED_ENCODING.charsetName();
    public static final String CONTENT_TYPE_HTML = "text/html; charset="+MOST_WIDELY_SUPPORTED_ENCODING.charsetName();
    public static final String CONTENT_TYPE_TEXT_PLAIN = "text/plain; charset="+MOST_WIDELY_SUPPORTED_ENCODING.charsetName();
    public static final String CONTENT_TYPE_JSON = "application/json; charset="+MOST_WIDELY_SUPPORTED_ENCODING.charsetName();
    
    /*
     * ########################################################
     * ## Convenience builders
     * ########################################################
     */
    
    public static ClasspathResourcesObject.Builder classpathResourcesAt(String pattern){
        return new ClasspathResourcesObject.Builder(DSL.class, pattern);
    }
	
    /* ######################################################## 
     * ## Hand-coded response factory methods
     * ########################################################
     */

    public static final Response OK(Representation r, HeaderField ... header){
        return new Response(ResponseCode.OK, r, header);
    }

    public static final Response CREATED(LocationField location){
        return new Response(ResponseCode.CREATED, null, location);
    }

    public static final Response NO_CONTENT(){
        return new Response(ResponseCode.NO_CONTENT, null);
    }

    public static final Response UNAUTHORIZED(WWWAuthenticateField authorization, Representation r){
        return new Response(ResponseCode.UNAUTHORIZED, r, authorization);
    }

    public static final Response SEE_OTHER(LocationField location, HeaderField ... header){
        return new Response(ResponseCode.SEE_OTHER, null, makeHeader(location, header));
    }
    
    public static final Response SEE_OTHER(LocationField location, Representation representation, HeaderField ... header){
        return new Response(ResponseCode.SEE_OTHER, representation, makeHeader(location, header));
    }

    /* ######################################################## 
     * ## Header factory methods
     * ########################################################
     */
    public static final WWWAuthenticateField BasicAuthentication(String realmName){
        return new WWWAuthenticateField(WWWAuthenticateField.Method.Basic, realmName);
    }
    public static final SetCookieField SetCookie(String name, String value){
        return new SetCookieField(name, value, null);
    }
    public static final LocationField Location(String uri){
        return new LocationField(uri);
    }
    
    /* ######################################################## 
     * ## Representation factory methods
     * ########################################################
     */

    public static final Representation Csv(String text){
        return new BinaryRepresentation(CONTENT_TYPE_CSV, new ByteArrayInputStream(getBytes(text, MOST_WIDELY_SUPPORTED_ENCODING)));
    }

    public static final Representation Html(String text){
        return new BinaryRepresentation(CONTENT_TYPE_HTML, new ByteArrayInputStream(getBytes(text, MOST_WIDELY_SUPPORTED_ENCODING)));
    }

    public static final Representation Text(String text){
        return new BinaryRepresentation(CONTENT_TYPE_TEXT_PLAIN, new ByteArrayInputStream(getBytes(text, MOST_WIDELY_SUPPORTED_ENCODING)));
    }

    public static final Representation Json(String text){
        return Json(new ByteArrayInputStream(getBytes(text, MOST_WIDELY_SUPPORTED_ENCODING)));
    }
    
    public static final Representation Json(InputStream text){
        return new BinaryRepresentation(CONTENT_TYPE_JSON, text);
    }
    
    /**
     * This sets up a mechanism for streaming Json using an OutputStream.
     * The response is created and data can begin streaming to clients before
     * The users of this method have finished writing to the provided outputstream. 
     * 
     * The Executors.defaultThreadFactory is used.  
     * TODO: If it may make sense create a thread pool or allow the user to configure one here.
     * @param jsonStream
     * @return
     */
    public static final Representation JsonStream(final Consumer<OutputStream> jsonStream){
    	final PipedOutputStream stream = new PipedOutputStream();
    	PipedInputStream result;
    	
    	try{
    		result = new PipedInputStream(stream);
    	}catch(Exception e){
    		throw new RuntimeException(e);
    	}
    	
    	Executors.defaultThreadFactory().newThread(
    		new Runnable(){
    			@Override
    			public void run() {
    				jsonStream.accept(stream);
    			};
    		}).start();
    	return Json(result);

    }
    
    

    public static final Representation HtmlFromClasspath(String name, Object context){
        return HtmlFromClasspath(name, context.getClass());
    }

    public static final Representation HtmlFromClasspath(String name, Class<?> clazz){
        return FromClasspath("text/html", name, clazz);
    }

    public static final Representation FromClasspath(String contentType, String name, final Class<?> clazz){
        return FromClasspath(contentType, name, new WrapperForInsecureClassloader(new ClassResourceLoader(clazz)));
    }
    
    private static final Representation FromClasspath(String contentType, String name, ResourceLoader loader){
        final InputStream stream = loader.getResourceAsStream(name);
        if(stream==null) throw new RuntimeException("No such resource on classpath: " + name);
        return Bytes(contentType, stream);
    }

    public static final Representation FromClasspath(String contentType, String name, Object context){
        return FromClasspath(contentType, name, context.getClass());
    }

    public static final Representation Bytes(String contentType, byte[] data){
        return new BinaryRepresentation(contentType, new ByteArrayInputStream(data));
    }

    public static final Representation Bytes(String contentType, InputStream data){
        return new BinaryRepresentation(contentType, data);
    }

    public static final Representation File(String contentType, java.io.File path){
        try {
            return Bytes("", new FileInputStream(path));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /* ########################################################
     * ## Public utility methods
     * ########################################################
     */

    public static final byte[] getBytes(String text, StandardCharset standardCharset) {
        try {
            //Every implementation of the Java platform is required to support the standard charsets
            //So no point in throwing a checked exception
            return text.getBytes(standardCharset.charsetName());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /* ########################################################
     * ## Utility methods
     * ########################################################
     */

    private static String toString(Throwable t) {
        final StringBuffer text = new StringBuffer(t.getClass().getName());
        final String message = t.getMessage();
        if(message!=null){
            text.append(": ");
            text.append(message);
        }

        for(StackTraceElement next : t.getStackTrace()){
            text.append("\n    at " + next.getClassName() + "." + next.getMethodName() + "(" + next.getFileName() + ":" +  next.getLineNumber() + ")");
        }
        return text.toString();
    }

    private static HeaderField[] makeHeader(HeaderField first, HeaderField... subsequent){
        List<HeaderField> items = new ArrayList<HeaderField>();
        items.add(first);
        if(subsequent!=null){
            items.addAll(Arrays.asList(subsequent));
        }
        return items.toArray(new HeaderField[items.size()]);
    }


    /* ######################################################## 
     * ## Generated 100 series responses
     * ########################################################
     */

    public static final Response CONTINUE(){
        return new Response(ResponseCode.CONTINUE, null);
    }
    public static final Response SWITCHING_PROTOCOLS(){
        return new Response(ResponseCode.SWITCHING_PROTOCOLS, null);
    }

    /* ######################################################## 
     * ## Generated 400 series responses
     * ########################################################
     */

    public static final Response BAD_REQUEST(){
        return new Response(ResponseCode.BAD_REQUEST, Text("400 Client Error: Bad Request"));
    }
    public static final Response BAD_REQUEST(Representation representation){
        return new Response(ResponseCode.BAD_REQUEST, representation);
    }
    public static final Response UNAUTHORIZED(){
        return new Response(ResponseCode.UNAUTHORIZED, Text("401 Client Error: Unauthorized"));
    }
    public static final Response UNAUTHORIZED(Representation representation){
        return new Response(ResponseCode.UNAUTHORIZED, representation);
    }
    public static final Response PAYMENT_REQUIRED(){
        return new Response(ResponseCode.PAYMENT_REQUIRED, Text("402 Client Error: Payment Required"));
    }
    public static final Response PAYMENT_REQUIRED(Representation representation){
        return new Response(ResponseCode.PAYMENT_REQUIRED, representation);
    }
    public static final Response FORBIDDEN(){
        return new Response(ResponseCode.FORBIDDEN, Text("403 Client Error: Forbidden"));
    }
    public static final Response FORBIDDEN(Representation representation){
        return new Response(ResponseCode.FORBIDDEN, representation);
    }
    public static final Response NOT_FOUND(){
        return new Response(ResponseCode.NOT_FOUND, Text("404 Client Error: Not Found"));
    }
    public static final Response NOT_FOUND(Representation representation){
        return new Response(ResponseCode.NOT_FOUND, representation);
    }
    @Deprecated
    /**
     * @deprecated This response code must include an Allow header. http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.4.6
     * @see DSL#METHOD_NOT_ALLOWED(org.httpobjects.util.Method...) 
     */
    public static final Response METHOD_NOT_ALLOWED(){
        return new Response(ResponseCode.METHOD_NOT_ALLOWED, Text("405 Client Error: Method Not Allowed"));
    }
    @Deprecated
    /**
     * @deprecated This response code must include an Allow header. http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.4.6
     * @see DSL#METHOD_NOT_ALLOWED(Representation, org.httpobjects.util.Method...)
     */
    public static final Response METHOD_NOT_ALLOWED(Representation representation){
        return new Response(ResponseCode.METHOD_NOT_ALLOWED, representation);
    }
    public static final Response METHOD_NOT_ALLOWED(Representation representation, Method... allowed){
        return new Response(ResponseCode.METHOD_NOT_ALLOWED, representation, new AllowField(allowed));
    }
    public static final Response METHOD_NOT_ALLOWED(Method... allowed) {
        return METHOD_NOT_ALLOWED(Text("405 Client Error: Method Not Allowed"), allowed);
    }
    public static final Response NOT_ACCEPTABLE(){
        return new Response(ResponseCode.NOT_ACCEPTABLE, Text("406 Client Error: Not Acceptable"));
    }
    public static final Response NOT_ACCEPTABLE(Representation representation){
        return new Response(ResponseCode.NOT_ACCEPTABLE, representation);
    }
    public static final Response PROXY_AUTHENTICATION_REQUIRED(){
        return new Response(ResponseCode.PROXY_AUTHENTICATION_REQUIRED, Text("407 Client Error: Proxy Authentication Required"));
    }
    public static final Response PROXY_AUTHENTICATION_REQUIRED(Representation representation){
        return new Response(ResponseCode.PROXY_AUTHENTICATION_REQUIRED, representation);
    }
    public static final Response REQUEST_TIMEOUT(){
        return new Response(ResponseCode.REQUEST_TIMEOUT, Text("408 Client Error: Request Timeout"));
    }
    public static final Response REQUEST_TIMEOUT(Representation representation){
        return new Response(ResponseCode.REQUEST_TIMEOUT, representation);
    }
    public static final Response CONFLICT(){
        return new Response(ResponseCode.CONFLICT, Text("409 Client Error: Conflict"));
    }
    public static final Response CONFLICT(Representation representation){
        return new Response(ResponseCode.CONFLICT, representation);
    }
    public static final Response GONE(){
        return new Response(ResponseCode.GONE, Text("410 Client Error: Gone"));
    }
    public static final Response GONE(Representation representation){
        return new Response(ResponseCode.GONE, representation);
    }
    public static final Response LENGTH_REQUIRED(){
        return new Response(ResponseCode.LENGTH_REQUIRED, Text("411 Client Error: Length Required"));
    }
    public static final Response LENGTH_REQUIRED(Representation representation){
        return new Response(ResponseCode.LENGTH_REQUIRED, representation);
    }
    public static final Response PRECONDITION_FAILED(){
        return new Response(ResponseCode.PRECONDITION_FAILED, Text("412 Client Error: Precondition Failed"));
    }
    public static final Response PRECONDITION_FAILED(Representation representation){
        return new Response(ResponseCode.PRECONDITION_FAILED, representation);
    }
    public static final Response REQUEST_ENTITY_TOO_LARGE(){
        return new Response(ResponseCode.REQUEST_ENTITY_TOO_LARGE, Text("413 Client Error: Request Entity Too Large"));
    }
    public static final Response REQUEST_ENTITY_TOO_LARGE(Representation representation){
        return new Response(ResponseCode.REQUEST_ENTITY_TOO_LARGE, representation);
    }
    public static final Response REQUEST_URI_TOO_LONG(){
        return new Response(ResponseCode.REQUEST_URI_TOO_LONG, Text("414 Client Error: Request-URI Too Long"));
    }
    public static final Response REQUEST_URI_TOO_LONG(Representation representation){
        return new Response(ResponseCode.REQUEST_URI_TOO_LONG, representation);
    }
    public static final Response UNSUPPORTED_MEDIA_TYPE(){
        return new Response(ResponseCode.UNSUPPORTED_MEDIA_TYPE, Text("415 Client Error: Unsupported Media Type"));
    }
    public static final Response UNSUPPORTED_MEDIA_TYPE(Representation representation){
        return new Response(ResponseCode.UNSUPPORTED_MEDIA_TYPE, representation);
    }
    public static final Response REQUESTED_RANGE_NOT_SATISFIABLE(){
        return new Response(ResponseCode.REQUESTED_RANGE_NOT_SATISFIABLE, Text("416 Client Error: Requested Range Not Satisfiable"));
    }
    public static final Response REQUESTED_RANGE_NOT_SATISFIABLE(Representation representation){
        return new Response(ResponseCode.REQUESTED_RANGE_NOT_SATISFIABLE, representation);
    }
    public static final Response EXPECTATION_FAILED(){
        return new Response(ResponseCode.EXPECTATION_FAILED, Text("417 Client Error: Expectation Failed"));
    }
    public static final Response EXPECTATION_FAILED(Representation representation){
        return new Response(ResponseCode.EXPECTATION_FAILED, representation);
    }
    public static final Response ACCEPTED(Representation representation, HeaderField ... headers) {
     return new Response(ResponseCode.ACCEPTED, representation,headers);
    }
    
    /* ######################################################## 
     * ## Generated 500 series responses
     * ########################################################
     */

    public static final Response INTERNAL_SERVER_ERROR(){
        return new Response(ResponseCode.INTERNAL_SERVER_ERROR, Text("500 Server Error: Internal Server Error"));
    }
    public static final Response INTERNAL_SERVER_ERROR(Representation representation){
        return new Response(ResponseCode.INTERNAL_SERVER_ERROR, representation);
    }
    public static final Response INTERNAL_SERVER_ERROR(Throwable t){
        return new Response(ResponseCode.INTERNAL_SERVER_ERROR, Text(toString(t)));
    }
    public static final Response NOT_IMPLEMENTED(){
        return new Response(ResponseCode.NOT_IMPLEMENTED, Text("501 Server Error: Not Implemented"));
    }
    public static final Response NOT_IMPLEMENTED(Representation representation){
        return new Response(ResponseCode.NOT_IMPLEMENTED, representation);
    }
    public static final Response NOT_IMPLEMENTED(Throwable t){
        return new Response(ResponseCode.NOT_IMPLEMENTED, Text(toString(t)));
    }
    public static final Response BAD_GATEWAY(){
        return new Response(ResponseCode.BAD_GATEWAY, Text("502 Server Error: Bad Gateway"));
    }
    public static final Response BAD_GATEWAY(Representation representation){
        return new Response(ResponseCode.BAD_GATEWAY, representation);
    }
    public static final Response BAD_GATEWAY(Throwable t){
        return new Response(ResponseCode.BAD_GATEWAY, Text(toString(t)));
    }
    public static final Response SERVICE_UNAVAILABLE(){
        return new Response(ResponseCode.SERVICE_UNAVAILABLE, Text("503 Server Error: Service Unavailable"));
    }
    public static final Response SERVICE_UNAVAILABLE(Representation representation){
        return new Response(ResponseCode.SERVICE_UNAVAILABLE, representation);
    }
    public static final Response SERVICE_UNAVAILABLE(Throwable t){
        return new Response(ResponseCode.SERVICE_UNAVAILABLE, Text(toString(t)));
    }
    public static final Response GATEWAY_TIMEOUT(){
        return new Response(ResponseCode.GATEWAY_TIMEOUT, Text("504 Server Error: Gateway Timeout"));
    }
    public static final Response GATEWAY_TIMEOUT(Representation representation){
        return new Response(ResponseCode.GATEWAY_TIMEOUT, representation);
    }
    public static final Response GATEWAY_TIMEOUT(Throwable t){
        return new Response(ResponseCode.GATEWAY_TIMEOUT, Text(toString(t)));
    }
    public static final Response HTTP_VERSION_NOT_SUPPORTED(){
        return new Response(ResponseCode.HTTP_VERSION_NOT_SUPPORTED, Text("505 Server Error: HTTP Version Not Supported"));
    }
    public static final Response HTTP_VERSION_NOT_SUPPORTED(Representation representation){
        return new Response(ResponseCode.HTTP_VERSION_NOT_SUPPORTED, representation);
    }
    public static final Response HTTP_VERSION_NOT_SUPPORTED(Throwable t){
        return new Response(ResponseCode.HTTP_VERSION_NOT_SUPPORTED, Text(toString(t)));
    };
}
