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
import java.util.Collections;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.httpobjects.HttpObject;
import org.httpobjects.header.GenericHeaderField;
import org.httpobjects.header.HeaderField;
import org.httpobjects.servlet.ServletMethodInvoker;
import org.mortbay.jetty.HttpConnection;
import org.mortbay.jetty.Request;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.AbstractHandler;


public class HttpObjectsJettyHandler extends AbstractHandler {
	private final ServletMethodInvoker invoker;
	
	public HttpObjectsJettyHandler(HttpObject ... objects) {
		this(Collections.<HeaderField>emptyList(), objects);
	}
	
	 
  public HttpObjectsJettyHandler(List<? extends HeaderField> defaultResponseHeaders, HttpObject ... objects) {
    invoker = new ServletMethodInvoker(defaultResponseHeaders, HttpObject.NOT_FOUND(HttpObject.Text("Error: NOT_FOUND")), objects);
  }
  
	@Override
	public void handle(String target, HttpServletRequest request, HttpServletResponse response, int dispatch) 
			throws IOException, ServletException {
		
		final boolean invocationHappened = invoker.invokeFirstPathMatchIfAble(target, request, response);
		
		if(invocationHappened)
			setHandled(request);     
		
	}

	private void setHandled(HttpServletRequest request) {
		Request base_request = (request instanceof Request) ? (Request)request:HttpConnection.getCurrentConnection().getRequest();
		base_request.setHandled(true);
	}
	
	
	public static Server launchServer(int port, HttpObject ... objects) {
		try {
			Server s = new Server(port);
			s.setHandler(new HttpObjectsJettyHandler(Collections.singletonList(new GenericHeaderField("Cache-Control", "no-cache")), objects));
			
			s.start();
			
			return s;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
