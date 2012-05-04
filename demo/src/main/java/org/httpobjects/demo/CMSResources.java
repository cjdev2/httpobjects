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
package org.httpobjects.demo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.httpobjects.HttpObject;
import org.httpobjects.Request;
import org.httpobjects.Response;



public class CMSResources extends HttpObject {
	private final File root;
	
	public CMSResources(File root) {
		super("/advertiser/{cid}/cms/content/{path*}");
		this.root = root;
	}
	
	@Override
	public Response get(Request req) {
		String cid = req.pathVars().valueFor("cid");
		String path = req.pathVars().valueFor("path");
		File localPath = (path==null || path.isEmpty())?root:new File(root, path);
		
		System.out.println(path);
		System.out.println(localPath);
		
		if(localPath.isFile()){
			String contentType = contentTypeOf(localPath);
			InputStream data = FileInputStream(localPath);
			
			return OK(Bytes(contentType, data));
		} else if(localPath.isDirectory()){
			return OK(Html(directoryListing(cid, localPath)));
		} else {
			return NOT_FOUND();
		}
	}
	
	private String directoryListing(String cid, File localPath) {
		StringBuffer text = new StringBuffer("<html><body><h1>Stuff specially for my favorite " + cid + "</h1>");
		text.append("<div><a href=\"..\">..</a></div><hr/>");
		for(File next : localPath.listFiles()){
			String s = next.isDirectory()?next.getName() + "/" : next.getName();
			text.append("<div><a href=\"" + s + "\">" + s + "</a></div>");
			text.append('\n');
		}
		text.append("</body></html>");
		return text.toString();
	}

	private FileInputStream FileInputStream(File localPath){
		try {
			return new FileInputStream(localPath);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
	private String contentTypeOf(File f){
		return "text/plain";
	}
}
