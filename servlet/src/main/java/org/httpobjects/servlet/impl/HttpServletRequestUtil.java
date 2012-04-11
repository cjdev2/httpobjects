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
package org.httpobjects.servlet.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

import org.httpobjects.header.HeaderField;
import org.httpobjects.header.OtherHeaderField;
import org.httpobjects.header.request.AuthorizationField;
import org.httpobjects.header.request.CookieField;
import org.httpobjects.header.request.RequestHeader;
import org.httpobjects.header.response.SetCookieField;
import org.httpobjects.header.response.WWWAuthenticateField.Method;

public class HttpServletRequestUtil {

	public static List<SetCookieField> buildCookies(HttpServletRequest request) {
		javax.servlet.http.Cookie[] servletCookies = request.getCookies();
		final List<SetCookieField> cookies;
		if(servletCookies==null){
			cookies = Collections.emptyList();
		}else{
			cookies = new ArrayList<SetCookieField>(servletCookies.length);
			for(javax.servlet.http.Cookie next : servletCookies){
				cookies.add(HttpServletRequestUtil.buildCookie(next));
			}
		}
		return cookies;
	}

	public static SetCookieField buildCookie(javax.servlet.http.Cookie next) {
		return new SetCookieField(
					next.getName(), 
					next.getValue(), 
					next.getDomain(),
					next.getPath(),
					null,
					next.getSecure());
	}

	public static RequestHeader buildHeader(HttpServletRequest request) {
		List<HeaderField> fields = new ArrayList<HeaderField>();
		
		@SuppressWarnings("unchecked")
		Enumeration<String> names = request.getHeaderNames();
		
		while(names.hasMoreElements()){
			String name = names.nextElement();
			if(name.equals("Authorization")){
				String fValue = request.getHeader(name);
				fValue = fValue.trim();
				StringTokenizer tokens = new StringTokenizer(fValue);
				
				fields.add(new AuthorizationField(Method.valueOf(tokens.nextToken()), tokens.nextToken()));
			}else if(name.equals("Cookie")){
				String fValue = request.getHeader(name);
				fields.add(new CookieField(fValue));
			}else{
				fields.add(new OtherHeaderField(name, request.getHeader(name)));
			}
		}
		return new RequestHeader(fields);
	}

}
