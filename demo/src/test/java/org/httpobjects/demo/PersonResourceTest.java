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

import static org.httpobjects.test.HttpObjectAssert.bodyOf;
import static org.httpobjects.test.HttpObjectAssert.contentTypeOf;
import static org.httpobjects.test.HttpObjectAssert.responseCodeOf;
import static org.junit.Assert.*;

import org.httpobjects.HttpObject;
import org.httpobjects.Request;
import org.httpobjects.Response;
import org.httpobjects.demo.PersonResource;
import org.httpobjects.test.MockRequest;
import org.junit.Test;



public class PersonResourceTest {
	
	@Test
	public void is_found_at_the_right_path(){
		PersonResource subject = new PersonResource();
		assertTrue(subject.pattern().matches("/people/stu"));
		assertTrue(subject.pattern().matches("/people/joe"));
		assertTrue(subject.pattern().matches("/people/"));
		assertFalse(subject.pattern().matches("/frogs"));
		assertFalse(subject.pattern().matches("/"));
	}
	
	
//	@Test
//	public void scottsmen_are_rejected(){
//		// given
//		HttpObject subject = new PersonResource();
//		Request input = new MockRequest(subject, "/people/stu");
//		User scottsmen = aUserFromScottland();
//		input.session << scottsmen
//
//		// when
//		Response output = subject.get(input);
//		
//		//then
//		assertTrue(responseCodeOf(output).isUNAUTHORIZED());
//		assertTrue(contentTypeOf(output).isPlainText());
//		assertTrue(bodyOf(output).equals("go home, scottsman!"));
//		
//	}
	
	@Test
	public void returns_stu(){
		// given
		HttpObject subject = new PersonResource();
		Request input = new MockRequest(subject, "/people/stu");
		
		// when
		Response output = subject.get(input);
		
		// then
		assertTrue(responseCodeOf(output).isOK_200());
		assertTrue(contentTypeOf(output).isPlainText());
		assertTrue(bodyOf(output).equals("stu"));
	}
		
	@Test
	public void returns_NOT_FOUND_when_name_is_missing(){
		// given
		PersonResource subject = new PersonResource();
		Request input = new MockRequest(subject, "/people/");
		
		// when
		Response output = subject.get(input);
		
		// then
		assertTrue(responseCodeOf(output).isNOT_FOUND());
		assertTrue(contentTypeOf(output).isPlainText());
		assertEquals("No such person", bodyOf(output).asString());
		
	}
	
}
