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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.httpobjects.Eventual;
import org.httpobjects.HttpObject;
import org.httpobjects.Request;
import org.httpobjects.Response;

public class FilesystemResourcesObject  extends HttpObject {
	private final File relativeTo;
	
	public FilesystemResourcesObject(String pathPattern, File relativeTo) {
		super(pathPattern, null);
		this.relativeTo = relativeTo;
	}
	
	@Override
	public Eventual<Response> get(Request req) {
		final String resource = req.path().valueFor("resource");
		if(isNullOrEmpty(resource) ||  resource.endsWith("/")) return null;
		
		File path = new File(relativeTo, resource);
		
		if(!isBelow(path, relativeTo)){
		    return null;
		}
		
		if(path.exists() && path.isFile()){
			return OK(Bytes(mimeTypeFor(resource), openStream(path)));
		}else{
			return null;
		}
	}
	
	private boolean isBelow(java.io.File path, java.io.File dir) {
	    try {
	        final File pdir = dir.getCanonicalFile();
	        File n = path.getCanonicalFile();
	        while((n = n.getParentFile())!=null){
	            if(pdir.equals(n)){
	                return true;
	            }
	        }
	        return false;
	    } catch (IOException e) {
	        throw new RuntimeException(e);
	    }
	}

    private InputStream openStream(File path){
		try {
			return new FileInputStream(path);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
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
}