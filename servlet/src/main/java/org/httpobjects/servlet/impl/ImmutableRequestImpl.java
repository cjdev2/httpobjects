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

import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.httpobjects.Representation;
import org.httpobjects.Request;
import org.httpobjects.header.request.RequestHeader;
import org.httpobjects.header.response.SetCookieField;
import org.httpobjects.path.PathVariables;
import org.httpobjects.util.RequestQueryUtil;

public class ImmutableRequestImpl implements Request {
	private final PathVariables vars;
	private final String contentType;
	private final List<SetCookieField> cookies;
	private final String query;
	private final Map<String, String> parameters;
	private final RequestHeader header;
	private final Representation representation;
	
	public ImmutableRequestImpl(PathVariables vars, HttpServletRequest request) {



		this.vars = vars;
		this.contentType = request.getContentType();
		this.cookies = Collections.unmodifiableList(HttpServletRequestUtil.buildCookies(request));



		this.query = request.getQueryString();


        try {
            this.parameters = Collections.unmodifiableMap(RequestQueryUtil.getUrlParameters(request.getQueryString()));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }


        this.header = HttpServletRequestUtil.buildHeader(request);



		this.representation = new ImmutableHttpServletRequestRepresentation(request);
	}


	@Override
	public PathVariables pathVars() {
		return vars;
	}
	
	public String contentType(){
		return contentType;
	}
	
	@Override
	public boolean hasRepresentation() {
		return true;
	}
	@Override
	public List<SetCookieField> cookies() {
		return cookies;
	}
	
	@Override
	public String query(){
		return query;
	}
	
	@Override
	public String getParameter(String name) {
		return parameters.get(name);
	}
	
	
	@Override
	public RequestHeader header() {
		return header;
	}
	
	
	@Override
	public Representation representation() {
		return representation;		
	}
	
	@Override
	public Request immutableCopy() {
		return this;
	}
}
