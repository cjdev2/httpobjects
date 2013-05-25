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
package org.httpobjects;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class QueryTest {

    
    @Test
    public void toStringIsNullSafe(){
        // given
        Query testSubject = new Query(null);
        
        // when
        String result = testSubject.toString();
        
        // then
        assertEquals("", result);
        
    }
    
    @Test
    public void toStringDoesWhatYouWouldExpect() {
        // given
        Query testSubject = new Query("?xyz=123&abc=456");
        
        // when
        final String result = testSubject.toString();
        
        // then
        Assert.assertEquals("?xyz=123&abc=456", result);
    }
    
    @Test
    public void readsParametersByName() {
        // given
        Query testSubject = new Query("?xyz=123&abc=456");
        
        // when
        final String abc = testSubject.valueFor("abc");
        final String xyz = testSubject.valueFor("xyz");
        
        // then
        assertEquals("123", xyz);
        assertEquals("456", abc);
    }
    

    @Test
    public void listsParameterNames() {
        // given
        Query testSubject = new Query("?xyz=123&abc=456");
        
        // when
        final List<String> names = testSubject.paramNames();
        
        // then
        assertEquals(2, names.size());
        assertEquals("xyz", names.get(0));
        assertEquals("abc", names.get(1));
    }

    @Test
    public void returnsEmptyListOfParamNamesWhenTheQueryStringIsEmpty() {
        // given
        Query testSubject = new Query("");
        
        // when
        final List<String> names = testSubject.paramNames();
        
        // then
        assertEquals(0, names.size());
    }
}
