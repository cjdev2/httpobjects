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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.httpobjects.header.HeaderField;
import org.httpobjects.header.response.LocationField;
import org.httpobjects.header.response.SetCookieField;
import org.httpobjects.header.response.WWWAuthenticateField;
import org.httpobjects.representation.BinaryRepresentation;

/**########################################################
 * ## DSL METHODS
 * ########################################################
 */
public class DSL {
	
	public static final Response OK(Representation r, HeaderField ... header){
		return new Response(ResponseCode.OK, r, header);
	}

    public static final Response NO_CONTENT(){
        return new Response(ResponseCode.NO_CONTENT, null);
    }

	public static final Response BAD_REQUEST(Representation r){
		return new Response(ResponseCode.BAD_REQUEST, r);
	}
	public static final Response BAD_REQUEST(){
		return new Response(ResponseCode.BAD_REQUEST, null);
	}
	
	public static final Response UNAUTHORIZED(WWWAuthenticateField authorization, Representation r){
		return new Response(ResponseCode.UNAUTHORIZED, r, authorization);
		
	}
	
	public static final Response UNAUTHORIZED(Representation r){
		return new Response(ResponseCode.UNAUTHORIZED, r);
	}
	
	public static final Response UNAUTHORIZED(){
		return new Response(ResponseCode.UNAUTHORIZED, null);
	}
	
	public static final Response NOT_FOUND(Representation r){
		return new Response(ResponseCode.NOT_FOUND, r);
	}
	
	public static final Response NOT_FOUND(){
		return new Response(ResponseCode.NOT_FOUND, null);
	}
	
	public static final Response BAD_GATEWAY(){
		return new Response(ResponseCode.BAD_GATEWAY, Text("Error!"));
	}
	
	public static final Response METHOD_NOT_ALLOWED(){
		return new Response(ResponseCode.METHOD_NOT_ALLOWED, Text("Error: Method not allowed."));
	}
	
	public static final Response SEE_OTHER(LocationField location, HeaderField ... header){
		return new Response(ResponseCode.SEE_OTHER, null, makeHeader(location, header));
	}
	public static final Response SEE_OTHER(LocationField location, Representation representation, HeaderField ... header){
		return new Response(ResponseCode.SEE_OTHER, representation, makeHeader(location, header));
	}

	public static final Response INTERNAL_SERVER_ERROR(Representation r){
		return new Response(ResponseCode.INTERNAL_SERVER_ERROR, r);
	}
	
	public static final Response INTERNAL_SERVER_ERROR(Throwable t){
		return new Response(ResponseCode.INTERNAL_SERVER_ERROR, Text(toString(t)));
	}
	
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
		return items.toArray(new HeaderField[]{});
	}
	public static final WWWAuthenticateField BasicAuthentication(String realmName){
		return new WWWAuthenticateField(WWWAuthenticateField.Method.Basic, realmName);
	}
	public static final SetCookieField SetCookie(String name, String value){
		return new SetCookieField(name, value, null);
	}
	public static final LocationField Location(String uri){
		return new LocationField(uri);
	}
	public static final Representation Html(String text){
		return new BinaryRepresentation("text/html", new ByteArrayInputStream(text.getBytes()));
	}
	
	public static final Representation Text(String text){
		return new BinaryRepresentation("text/plain", new ByteArrayInputStream(text.getBytes()));
	}
	
	public static final Representation Json(String text){
		return new BinaryRepresentation("application/json", new ByteArrayInputStream(text.getBytes()));
	}
	
	public static final Representation HtmlFromClasspath(String name, Object context){
		return HtmlFromClasspath(name, context.getClass());
	}
	
	public static final Representation HtmlFromClasspath(String name, Class<?> clazz){
		return FromClasspath("text/html", name, clazz);
	}
	

	public static final Representation FromClasspath(String contentType, String name, Class<?> clazz){
		return Bytes(contentType, clazz.getResourceAsStream(name));
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
}
