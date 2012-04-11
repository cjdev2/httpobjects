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

import javax.servlet.http.HttpServletRequest;

import org.httpobjects.Representation;

import java.io.*;

public class ImmutableHttpServletRequestRepresentation implements Representation {
	private final String contentType;
    private final byte[] content;

	public ImmutableHttpServletRequestRepresentation(HttpServletRequest request) {
		super();

        try{
            InputStream in = request.getInputStream();
            if(in!=null){
               ByteArrayOutputStream out = new ByteArrayOutputStream();

                copy(in, out);

                content = out.toByteArray();
            }else{
                content = new byte[]{};
            }

        } catch(Exception e){
            throw new RuntimeException(e);
        }
        contentType = request.getContentType();

	}


    private static void copy(InputStream in, OutputStream out) {
        try {

			byte[] buffer = new byte[1024 * 10];

			for(int x=in.read(buffer);x!=-1;x=in.read(buffer)){
				out.write(buffer, 0, x);
			}
			out.close();
			in.close();

		} catch (IOException e) {
			throw new RuntimeException(e);
		}
    }

    @Override
	public void write(OutputStream out) {
         copy(new ByteArrayInputStream(content), out);
	}
	
	@Override
	public String contentType() {
		return contentType;
	}
}
