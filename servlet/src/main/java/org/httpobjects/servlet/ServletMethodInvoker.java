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
package org.httpobjects.servlet;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.httpobjects.HttpObject;
import org.httpobjects.Request;
import org.httpobjects.Response;
import org.httpobjects.header.GenericHeaderField;
import org.httpobjects.header.HeaderField;
import org.httpobjects.header.HeaderFieldVisitor;
import org.httpobjects.header.request.AuthorizationField;
import org.httpobjects.header.request.CookieField;
import org.httpobjects.header.response.AllowField;
import org.httpobjects.header.response.LocationField;
import org.httpobjects.header.response.SetCookieField;
import org.httpobjects.header.response.WWWAuthenticateField;
import org.httpobjects.servlet.impl.LazyRequestImpl;
import org.httpobjects.util.HttpObjectUtil;
import org.httpobjects.util.Method;
import scala.concurrent.Future;

public class ServletMethodInvoker {
	private final HttpObject[] objects;
	private final Response notFoundResponse;
	private final List<? extends HeaderField> defaultResponseHeaders;
    private final PathMatchObserver pathMatchObserver;
	
	public ServletMethodInvoker(HttpObject[] objects) {
		this(HttpObject.NOT_FOUND(HttpObject.Text("Error: NOT_FOUND")), objects);
	}
  public ServletMethodInvoker(Response notFoundResponse, HttpObject[] objects) {
    this(Collections.<HeaderField>emptyList(), notFoundResponse, objects);
  }

    public ServletMethodInvoker(List<? extends HeaderField> defaultResponseHeader, Response notFoundResponse, HttpObject[] objects) {
        this(PathMatchObserver.DO_NOTHING, defaultResponseHeader, notFoundResponse, objects);
    }

    public ServletMethodInvoker(PathMatchObserver pathMatchObserver, List<? extends HeaderField> defaultResponseHeader, Response notFoundResponse, HttpObject[] objects) {
        this.pathMatchObserver = pathMatchObserver;
        this.notFoundResponse = notFoundResponse;
        this.objects = objects;
        this.defaultResponseHeaders = defaultResponseHeader;
    }

    public boolean invokeFirstPathMatchIfAble(String path, HttpServletRequest r, HttpServletResponse httpResponse) {
        Future<Response> lastResponse = null;
				
        for (HttpObject next : objects) {
            pathMatchObserver.checkingPathAgainstPattern(path, next.pattern());
            if (next.pattern().matches(path)) {
                lastResponse = invoke(r, httpResponse, next);
                if (lastResponse != null) {
                    pathMatchObserver.pathMatchedPattern(path, next.pattern());
                    returnResponse(lastResponse, httpResponse);
                    break;
                }
            }
        }

        if (lastResponse != null) {
            return true;
        } else if (notFoundResponse != null) {
            returnResponse(notFoundResponse, httpResponse);
            return true;
        } else {
            return false;
        }
    }

    private Future<Response> invoke(HttpServletRequest r, HttpServletResponse httpResponse, HttpObject object) {
		final Method m = Method.fromString(r.getMethod());
		final Request input = new LazyRequestImpl(object.pattern().match(r.getRequestURI()), r);

		return HttpObjectUtil.invokeMethod(object, m, input);
	}


	private void returnResponse(Response r, final HttpServletResponse resp)  {

		try {
			resp.setStatus(r.code().value());
			
			for(HeaderField next : r.header()){
				next.accept(new HeaderFieldVisitor<Void>() {
					
					@Override
					public Void visit(CookieField cookieField) {
						resp.setHeader(cookieField.name(), cookieField.value());
						return null;
					}
					
					@Override
					public Void visit(GenericHeaderField other) {
						resp.setHeader(other.name(), other.value());
						return null;
					}

                    @Override
                    public Void visit(AllowField allowField) {
                        resp.setHeader(allowField.name(), allowField.value());
                        return null;
                    }

                    @Override
					public Void visit(LocationField location) {
						resp.setHeader(location.name(), location.value());
						return null;
					}
					
					@Override
					public Void visit(SetCookieField setCookieField) {
						resp.addCookie(translate(setCookieField));
						return null;
					}
					
					@Override
					public Void visit(WWWAuthenticateField wwwAuthorizationField) {
						resp.setHeader(wwwAuthorizationField.name(), wwwAuthorizationField.value());
						return null;
					}
					@Override
					public Void visit(AuthorizationField authorizationField) {
						throw new RuntimeException("Illegal header for request: " + authorizationField.getClass());
					}
					
				});
			}
			
			addDefaultHeadersAsApplicable(r, resp);
			
			if(r.hasRepresentation()){
				resp.setContentType(r.representation().contentType());
				OutputStream out = resp.getOutputStream();
				r.representation().write(out);
				out.close();
			}
			
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
  private void addDefaultHeadersAsApplicable(final Response r, final HttpServletResponse resp) {
    for(HeaderField defaultHeader : defaultResponseHeaders){
      boolean exists = false;
      for(HeaderField header : r.header()){
          if(header.name().equals(defaultHeader.name())){
            exists = true;
          }
      }
      
      if(!exists){
          resp.setHeader(defaultHeader.name(), defaultHeader.value());
      }
    }
  }
  
	private Cookie translate(SetCookieField cookie) {
	    Cookie c = new Cookie(cookie.name, cookie.value);
	    
	    if(cookie.domain!=null){
	        c.setDomain(cookie.domain);
	    }
//	    if(cookie.expiration!=null){
//	        c.setMaxAge(Integer.parseInt(cookie.expiration));
//	    }
	    if(cookie.path!=null){
	        c.setPath(cookie.path);
	    }
	    if(cookie.secure!=null) {
	        c.setSecure(cookie.secure);
	    }
	    
	    return c;
	}
}
