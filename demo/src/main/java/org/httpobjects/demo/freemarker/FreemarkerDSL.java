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
package org.httpobjects.demo.freemarker;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.httpobjects.Representation;

import freemarker.template.Configuration;
import freemarker.template.Template;

public final class FreemarkerDSL {
	public static Representation FreemarkerTemplate(final String template, final Object model, final Configuration config){
		return new Representation() {
			
			@Override
			public void write(OutputStream out) {
				try {
					Template t = config.getTemplate(template);
					Writer w = new OutputStreamWriter(out, t.getEncoding());
					config.getTemplate(template).process(model, w);
					w.close();
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
				
			}
			
			@Override
			public String contentType() {
				return null;
			}
		};
	}
}
