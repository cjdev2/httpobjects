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
package org.httpobjects.test;

import static org.httpobjects.DSL.Text;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.httpobjects.HttpObject;
import org.httpobjects.Query;
import org.httpobjects.header.GenericHeaderField;
import org.httpobjects.header.HeaderField;
import org.httpobjects.header.request.Cookie;
import org.httpobjects.header.request.CookieField;
import org.httpobjects.util.HttpObjectUtil;
import org.junit.Test;

public class MockRequestTest {

    @Test
    public void theQueryStringDefaultsToTheNullForm(){
        // given
        HttpObject o = new HttpObject("/foo");
        
        // when
        MockRequest r = new MockRequest(o, "/foo");
        
        // then
        assertEquals("", r.query().toString());
    }
    
    @Test
    public void allowsEverythingToBeSpecifiedAtOnce(){
        // given
        HttpObject o = new HttpObject("/messages/welcome");
        
        // when
        MockRequest r = new MockRequest(o, 
                                "/messages/welcome", new Query("?foo=bar"),
                                Text("hello world"),
                                new CookieField(new Cookie("name", "sally")),
                                new GenericHeaderField("xyz", "123"));
            
        // then
        assertEquals("/messages/welcome", r.path().toString());
        assertEquals("?foo=bar", r.query().toString());
        assertEquals("hello world", HttpObjectUtil.toAscii(r.representation()));
        
        final List<HeaderField> fields = r.header().fields();
        assertEquals(2, fields.size());
        assertEquals("Cookie: name=sally", toString(fields.get(0)));
        assertEquals("xyz: 123", toString(fields.get(1)));
    }
    
    private static String toString(HeaderField field){
        return field.name() + ": " + field.value();
    }
    
    @Test
    public void failsFastWhenGivenPathsThatDontMatchTheObjectsPattern(){
        // given
        HttpObject o = new HttpObject("/foo");
        
        // when
        Exception err = null;
        try{
            new MockRequest(o, "/bar");
        }catch(Exception e){
            err = e;
        }
        
        // then
        assertNotNull(err);
        assertTrue(err instanceof RuntimeException);
        assertEquals(
              "/foo does not match /bar", 
              ((RuntimeException)err).getMessage());
    }
    
    @Test
    public void failsFastWhenGivenQueryStringsInThePath(){
        // given
        HttpObject o = new HttpObject("/foo");
        
        // when
        Exception err = null;
        try{
            new MockRequest(o, "/foo?foo=bar");
        }catch(Exception e){
            err = e;
        }
        
        // then
        assertNotNull(err);
        assertTrue(err instanceof RuntimeException);
        assertEquals(
              "The query is in the path, but I wasn't expecting that; try using a constructor that takes a org.httpobjects.Query argument.", 
              ((RuntimeException)err).getMessage());
    }
    
    @Test
    public void allowsQueryStringsToBeMocked(){
        // given
        HttpObject o = new HttpObject("/foo");
        
        // when
        MockRequest r = new MockRequest(o, "/foo", new Query("?foo=bar"));
            
        // then
        assertEquals("?foo=bar", r.query().toString());
    }
    
}
