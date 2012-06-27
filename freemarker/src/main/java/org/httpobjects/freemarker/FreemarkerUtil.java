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
package org.httpobjects.freemarker;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Locale;
import java.util.regex.Pattern;

import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;

public class FreemarkerUtil {

	public static Configuration basicConfig(final Class<?> relativeTo){
		final Configuration cfg = new Configuration();
		cfg.setTemplateLoader(new TemplateLoader() {
			
			@Override
			public Reader getReader(Object source, String encoding) throws IOException {
				final String classpathResource = source.toString();
				return new InputStreamReader(relativeTo.getResourceAsStream(classpathResource), encoding);
			}
			
			@Override
			public long getLastModified(Object arg0) {
				return System.currentTimeMillis();
			}
			
			@Override
			public Object findTemplateSource(String name) throws IOException {
				return name.replaceAll(Pattern.quote("_en_US"), "");
			}
			
			@Override
			public void closeTemplateSource(Object arg0) throws IOException {
				
			}
		});
		cfg.setEncoding(Locale.US, "UTF8");
		cfg.setObjectWrapper(new DefaultObjectWrapper()); 
//		cfg.setTagSyntax(Configuration.SQUARE_BRACKET_TAG_SYNTAX);
		return cfg;
	}
}
