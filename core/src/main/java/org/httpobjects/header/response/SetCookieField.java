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
package org.httpobjects.header.response;

import java.util.StringTokenizer;

import org.httpobjects.header.HeaderField;
import org.httpobjects.header.HeaderFieldVisitor;

public class SetCookieField extends HeaderField {
	public static final SetCookieField fromHeaderValue(String header){
		StringTokenizer s = new StringTokenizer(header);
		NameValue nameValue = NameValue.parse(s.nextToken(";"));

		String domain = null;
		String path = null;
		String expiration = null;
		Boolean secure = null;

		while(s.hasMoreTokens()){
			String next = s.nextToken(";");
			if(next!=null){
				next = next.trim();
				if(next.equals("Secure")){
					secure = true;
				}else{
					NameValue property = NameValue.parse(next.trim());
					String name = property.name;

					if(name.equals("Domain")){
						domain = property.value;
					}else if(name.equals("Path")){
						path = property.value;
					}else if(name.equals("Expires")){
						expiration = property.value;
					}
				}
			}
		}

		return new SetCookieField(nameValue.name, nameValue.value, domain, path, expiration, secure);
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

	public final String name;
    public final String value;
    public final String domain;
    public final String path;
    public final String expiration;
    public final Boolean secure;

    public SetCookieField(String name, String value, String domain, String path,
			String expiration, Boolean secure) {
		super();
		this.name = name;
		this.value = value;
		this.domain = domain;
		this.path = path;
		this.expiration = expiration;
		this.secure = secure;
	}
    
    public SetCookieField(String name, String value, String domain) {
		this(name, value, domain, null, null, null);
	}
    
	@Override
	public <T> T accept(HeaderFieldVisitor<T> visitor) {
		return visitor.visit(this);
	}

    @Override
    public String name() {
        return "Set-Cookie";
    }

    @Override
    public String value() {
        return toString();
    }
    

	@Override
	public String toString() {
		String base = name + "=" + value;
		
		String secure = (this.secure!=null && this.secure)?" Secure;":"";
		
		
		String tail = appendFieldIfNotNull("Domain", domain) + 
		appendFieldIfNotNull("Path", path) + 
		appendFieldIfNotNull("Expires", expiration) + 
		secure;
		
		return tail.isEmpty()?base:base + ";" + tail;
	}

	private String appendFieldIfNotNull(String name, String value) {
		return value==null?"":" " + name + "=" + value + ";";
	}
}
