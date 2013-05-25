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
package org.httpobjects.path;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class PathPatternTest {

	@Test
	public void aRootPatternMatchesRoot(){
		// given 
		PathPattern p = new PathPattern("/");
		
		// when
		Path r = p.match("/");
		
		// then
		assertNotNull(r);
		assertEquals(0, r.size());
	}
	
	@Test
	public void aRootPatternDoesntMatchChildren(){
		assertFalse(new PathPattern("/").matches("/test"));
	}

	@Test
	public void aTopLevelItemPatternDoesntMatchRoot(){
		Path r = new PathPattern("/test").match("/");
		assertNull(r);
	}
	
	@Test
	public void doesntMatchSubPathsUnlessSpecified(){
		assertFalse(new PathPattern("/test").matches("/test/123"));
	}
	
	@Test
	public void parsesPathVariablesOutOfATwoLevelPatternWithTwoVars(){
		Path r = new PathPattern("/{apple}/{orange}").match("/jane/doe");
		assertNotNull(r);
		assertEquals(2, r.size());
		assertEquals("jane", r.valueFor("apple"));
		assertEquals("doe", r.valueFor("orange"));
	}

    @Test
	public void justChecking(){
		Path r = new PathPattern("/advertiser/{cid}/cms/data/logo.jpeg").match("/advertiser/3042233/cms/data/logo.jpeg");
		assertNotNull(r);
		assertEquals(1, r.size());
		assertEquals("3042233", r.valueFor("cid"));
	}

	@Test
	public void doesntChokeOnQueryString(){
		Path r = new PathPattern("/{apple}/{orange}").match("/jane/doe?flavor=chocolate");
		assertNotNull(r);
		assertEquals(2, r.size());
		assertEquals("jane", r.valueFor("apple"));
		assertEquals("doe", r.valueFor("orange"));
	}
	
	@Test
	public void parsesThePathVariableOutOfAOneLevelPatternWithOneVar(){
		Path r = new PathPattern("/{apple}/").match("/jane/");
		assertNotNull(r);
		assertEquals(1, r.size());
		assertEquals("jane", r.valueFor("apple"));
	}
	
	@Test
	public void toplevelWildcardPatternMatchesSubpaths(){
		Path r = new PathPattern("/{apple*}").match("/jane/is/cool");
		assertNotNull(r);
		assertEquals(1, r.size());
		assertEquals("jane/is/cool", r.valueFor("apple"));
	}
	
	@Test
	public void secondlevelWildcardPatternMatchesSubpaths(){
		Path r = new PathPattern("/house/{apple*}").match("/house/");
		assertNotNull(r);
		assertEquals(1, r.size());
		assertNull(r.valueFor("apple"));
	}
	
	@Test
	public void returnsRawPath(){
		PathPattern p = new PathPattern("/house/{apple*}");
		assertEquals("/house/{apple*}", p.raw());
		
	}
}
