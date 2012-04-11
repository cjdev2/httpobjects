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
package org.httpobjects.header.request;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

import org.httpobjects.header.HeaderField;
import org.httpobjects.header.HeaderFieldVisitor;

public class CookieField extends HeaderField {
	
	private List<Cookie> cookies = new ArrayList<Cookie>();
	
	public CookieField(String fieldValue) {
		StringTokenizer s = new StringTokenizer(fieldValue);
		
		String name = null;
		String value = null;
		String domain = null;
		String path = null;

		while(s.hasMoreTokens()){
			String next = s.nextToken(";");
			if(next!=null){
				next = next.trim();
				NameValue property = NameValue.parse(next.trim());

				if(property.name.equals("$Domain")){
					domain = property.value;
				}else if(property.name.equals("$Path")){
					path = property.value;
				}else {
					// Start of a new cookie .. record the previous one
					if(name!=null){
						cookies.add(new Cookie(name, value, path, domain));
					}
					name = property.name;
					value = property.value;
					domain = null;
					path = null;
				}
			}
		}
		
		if(name!=null){
			cookies.add(new Cookie(name, value, path, domain));
		}
		
		
	}
	
	
	private static class NameValue {
		public static NameValue parse(String nameValue){
			try {
				int pos = nameValue.indexOf('=');
				String name = nameValue.substring(0, pos);
				String value = nameValue.substring(pos + 1);
				return new NameValue(name, value);
			} catch (Exception e) {
				throw new RuntimeException("Error parsing " + nameValue);
			}
		}
		private final String name, value;

		public NameValue(String name, String value) {
			super();
			this.name = name;
			this.value = value;
		}
	}
		
	public CookieField(Cookie ... cookies) {
		if(cookies!=null){
			this.cookies.addAll(Arrays.asList(cookies));
		}
	}
	
	@Override
	public <T> T accept(HeaderFieldVisitor<T> visitor) {
		return visitor.visit(this);
	}

	@Override
	public String name() {
		return "Cookie";
	}
	
	public List<Cookie> cookies() {
		return cookies;
	}
	
	@Override
	public String value() {
		StringBuilder text = new StringBuilder();
		
		for(int x=0;x<cookies.size();x++){
			Cookie next = cookies.get(x);
			if(x!=0){
				text.append(";");
			}
			text.append(next.name + "=" + next.value);
			if(next.domain!=null){
				text.append(";$Domain=" + next.domain);
			}
			if(next.path!=null){
				text.append(";$Path=" + next.path);
			}
		}
		
		return text.toString();
	}
	
}
