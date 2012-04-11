/**
 * Copyright (C) 2011, 2012 Stuart Penrose
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
package org.httpobjects.test;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.httpobjects.Response;
import org.httpobjects.header.DefaultHeaderFieldVisitor;
import org.httpobjects.header.HeaderField;
import org.httpobjects.header.OtherHeaderField;
import org.httpobjects.header.response.LocationField;
import org.httpobjects.header.response.SetCookieField;

public class HttpObjectAssert {
	
	public static class ResponseCode {
		private final Response response; 

		private ResponseCode(Response response) {
			super();
			this.response = response;
		}
		public void assertIsOK_200() {
			if(!isOK_200()) throw new Error("expected 200 but was " + response.code());
		}
		
		public void assertIs(org.httpobjects.ResponseCode expected){
			if(response.code()!=expected){
				throw new Error("expected " + expected + " but was " + response.code());
			}
		}
		
		public boolean isOK_200() {
			return response.code() == org.httpobjects.ResponseCode.OK;
		}

		public boolean isNOT_FOUND() {
			return response.code() == org.httpobjects.ResponseCode.NOT_FOUND;
		}
	}
	
	
	public static class Cookies{
		private final Response response;

		public Cookies(Response response) {
			this.response = response;
		}
		
		public void assertContains(String name, String value){
			boolean found = false;
			final List<SetCookieField> cookies = new ArrayList<SetCookieField>();
			for(HeaderField next : response.header()){
				SetCookieField c = next.accept(new DefaultHeaderFieldVisitor<SetCookieField>(){
					@Override
					public SetCookieField visit(SetCookieField setCookieField) {
						cookies.add(setCookieField);
						return setCookieField;
					}
				});
				
				if(c!=null && (c.name.equals(name) && c.value.equals(value))){
					found = true;
				}
			}
			if(!found){
				StringBuffer text = new StringBuffer("No such cookie: " + name + "=" + value + ".);");
				if(cookies.isEmpty()){
					text.append("There were no cookies at all, in fact.");
				}else{
					text.append("Cookies present are: ");
					for(SetCookieField next : cookies){
						text.append("\n  Cookie: " + next);
					}
				}
				throw new RuntimeException(text.toString());
			}
		}
		
		
	}
	
	public static class Header {
		private final Response response;

		public Header(Response response) {
			super();
			this.response = response;
		} 

		public void assertContainsCustomHeader(final String name, final String value){
			boolean found = false;
			for(HeaderField h : response.header()){
				boolean matches = h.accept(new DefaultHeaderFieldVisitor<Boolean>(){
					@Override
					public Boolean visit(OtherHeaderField custom) {
						return custom.name().equals(name) && custom.value().equals(value);
					}
					@Override
					protected Boolean defaultValue() {
						return false;
					}
				});
				if(matches) found=true;
			}
			
			if(!found){
				throw new RuntimeException("Could not find header: " + name + "=" + value);
			}
		}
		
	}
	
	public static class LocationHeader{
		private final Response response;

		public LocationHeader(Response response) {
			super();
			this.response = response;
		} 
		
		public String value(){
			for(HeaderField h : response.header()){
				String value = h.accept(new DefaultHeaderFieldVisitor<String>(){
					@Override
					public String visit(LocationField location) {
						return location.value();
					}
				});
				if(value!=null) return value;
			}
			
			return null;
		}

		public void assertIs(String expected) {
			String value = value();
			if(value==null || !value.equals(expected)){
				throw new RuntimeException("Expected \"" + expected + "\" but was \"" + value + "\"");
			}
		}
	}
	
	public static class ContentType {
		private final Response response; 
		
		private ContentType(Response response) {
			super();
			this.response = response;
		}

		public boolean isPlainText() {
			return response.hasRepresentation() && response.representation().contentType().equals("text/plain");
		}
		
		public void assertIs(String expectation){
			String value = toString();
			if(
					((value==null || expectation ==null) && value!=expectation) 
					||
					(value!=null && !value.equals(expectation))){
				throw new RuntimeException("Expected \"" + expectation + "\" but was \"" + value + "\"");
			}
		}
		
		public String toString(){
			return response.hasRepresentation() ? response.representation().contentType():"";
		}
	}
	public static class Representation {
		private final Response response; 
		
		private Representation(Response response) {
			super();
			this.response = response;
		}
		
		public boolean equals(String text) {
			return response.hasRepresentation() && textOf(response.representation()).equals(text);
		}
		
		private String textOf(org.httpobjects.Representation r){
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			r.write(out);
			return new String(out.toByteArray());
		}

		public Object asString() {
			return response.hasRepresentation()?textOf(response.representation()):null;
		}
	}
	
	public static ResponseCode responseCodeOf(Response output) {
		return new ResponseCode(output);
	}
	public static LocationHeader locationHeaderOf(Response output) {
		return new LocationHeader(output);
	}
	
	public static Header headerOf(Response output){
		return new Header(output);
	}
	
	public static Cookies cookiesIn(Response output){
		return new Cookies(output);
	}
	public static ContentType contentTypeOf(Response output) {
		return new ContentType(output);
	}

	public static Representation bodyOf(Response output) {
		return new Representation(output);
	}

}
