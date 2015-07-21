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
package org.httpobjects.util;

import java.io.InputStream;

import org.httpobjects.HttpObject;
import org.httpobjects.Request;
import org.httpobjects.Eventual;
import org.httpobjects.Response;
import org.httpobjects.impl.fn.FunctionalJava;
import org.httpobjects.util.impl.ClassResourceLoader;
import org.httpobjects.util.impl.ResourceLoader;
import org.httpobjects.util.impl.WrapperForInsecureClassloader;

public class ClasspathResourcesObject  extends HttpObject {
    private static final String PATH_VAR_NAME = "resource";
	private final ResourceLoader loader;
	private final String prefix;
	
	public ClasspathResourcesObject(String pathPattern, Class<?> relativeTo) {
		this(pathPattern, relativeTo, "");
	}

	public ClasspathResourcesObject(String pathPattern, Class<?> relativeTo, String prefix) {
		super(pathPattern, null);
		
		if(!hasExpectedPathVar()) {
			throw new RuntimeException("Must have a path var named '" + PATH_VAR_NAME + 
			                            "', but there is none in '" + pathPattern + 
			                            "'.  Hint: maybe you meant '" + pathPattern + "/{resource*}' ?"); 
		}
		this.prefix = (!prefix.equals("") && !prefix.endsWith("/"))? prefix + "/" : prefix;
		this.loader = new WrapperForInsecureClassloader(new ClassResourceLoader(relativeTo));
	}

	
	
	private boolean hasExpectedPathVar() {
       return FunctionalJava
            .asSeq(pattern().varNames())
            .contains(PATH_VAR_NAME);
	}

	@Override
	public Eventual<Response> get(Request req) {
		final String resource = req.path().valueFor(PATH_VAR_NAME);
		if(isNullOrEmpty(resource) ||  resource.endsWith("/")) return null;
		
		final InputStream data = loader.getResourceAsStream(prefix + resource);
		
		if(data!=null){
			return OK(Bytes(mimeTypeFor(resource), data));
		}else{
			return null;
		}
	}

	private static String mimeTypeFor(String resource){
		return ieCompat(new MimeTypeTool().guessMimeTypeFromName(resource));
	}

	private static String ieCompat(String t) {
		return t.equals("text/html")?"text/html;charset=utf-8":t;
	}

	private boolean isNullOrEmpty(String t){
		return t==null || t.trim().isEmpty();
	}
	
	public static final class Builder {
		final Class<?> clazz;
		final String resourcePattern;
		
		public Builder(Class<?> clazz, String resourcePattern) {
			super();
			this.clazz = clazz;
			this.resourcePattern = resourcePattern;
		}

		public ClasspathResourcesObject servedAt(String pathPattern) {
			final String p;
			if(pathPattern.endsWith("/")){
				p = pathPattern.substring(0, pathPattern.length()-1);
			}else{
				p = pathPattern;
			}
			return new ClasspathResourcesObject(p + "/{resource*}", clazz, resourcePattern);
		}

		public Builder loadedVia(Class<?> clazz) {
			return new Builder(clazz, resourcePattern);
		}
		
	}

}