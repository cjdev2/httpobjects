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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;


public class MimeTypeToolTest {
	@Test
	public void detectsCommonFilenames(){
		final Mapping[] mappings = {
				map("text/html", ".html", ".htm"),
				map("text/javascript", ".js"),
				map("text/css", ".css"),
				map("image/png", ".png"),
				map("image/jpeg", ".jpg", ".jpeg"),
				map("image/gif", ".gif")
		};
		
		MimeTypeTool tool = new MimeTypeTool();
		
		for(Mapping mapping : mappings){
			for(String extension:mapping.extensions){
				String result = tool.guessMimeTypeFromName("xyz123" + extension);
				Assert.assertEquals(mapping.mimeType, result);
			}
		}
	}
	
	
	private Mapping map(String mimeType, String ... extensions){
		return new Mapping(mimeType, extensions);
	}
	
	static class Mapping {
		final String mimeType;
		final List<String> extensions;
		public Mapping(String mimeType, String ... extensions) {
			super();
			this.mimeType = mimeType;
			this.extensions = Collections.unmodifiableList(Arrays.asList(extensions));
		}
		
		
	}
}
