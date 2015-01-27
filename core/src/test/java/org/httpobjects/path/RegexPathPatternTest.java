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

import static java.util.Arrays.asList;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

public class RegexPathPatternTest {
    @Test
    public void rawPattern() {
        // given
        final PathPattern pattern = new RegexPathPattern(
                                Pattern.compile("\\/names\\/([a-zA-Z]*)\\/formulas\\/([a-zA-Z0-9/*+-=]*)\\/text"));
        // when
        final String raw = pattern.raw();
        
        // then
        assertEquals("\\/names\\/([a-zA-Z]*)\\/formulas\\/([a-zA-Z0-9/*+-=]*)\\/text", raw);
    }
    
    @Test
    public void happyPathByIndex() {
        // given
        final PathPattern pattern = new RegexPathPattern(
                                Pattern.compile("\\/names\\/([a-zA-Z]*)\\/formulas\\/([a-zA-Z0-9/*+-=]*)\\/text"));
        
        // when
        assertTrue(pattern.matches("/names/sally/formulas/1+1=2/text"));
        assertTrue(pattern.matches("/names/sally/formulas/3*3=9/text"));
        assertFalse(pattern.matches("/ages/sally/formulas/1+1=2/text"));
        assertListsEqual(toNames(asList("0", "1")), pattern.varNames());
        final Path p = pattern.match("/names/sally/formulas/1+1=2/text");
        assertEquals("/names/sally/formulas/1+1=2/text", p.toString());
        assertEquals("sally", p.valueFor("0"));
        assertEquals("1+1=2", p.valueFor("1"));
    }
    
    @Test
    public void happyPathByVarName() {
        // given
        final PathPattern pattern = new RegexPathPattern(
                                Pattern.compile("\\/names\\/([a-zA-Z]*)\\/formulas\\/([a-zA-Z0-9/*+-=]*)\\/text"),
                                "name",
                                "formula");
        
        // when
        assertTrue(pattern.matches("/names/sally/formulas/1+1=2/text"));
        assertTrue(pattern.matches("/names/sally/formulas/3*3=9/text"));
        assertFalse(pattern.matches("/ages/sally/formulas/1+1=2/text"));
        assertListsEqual(toNames(asList("name", "formula")), pattern.varNames());
        final Path p = pattern.match("/names/sally/formulas/1+1=2/text");
        assertEquals("/names/sally/formulas/1+1=2/text", p.toString());
        assertEquals("sally", p.valueFor("name"));
        assertEquals("1+1=2", p.valueFor("formula"));
    }
    

    private static List<PathParamName> toNames(List<String> strings) {
        List<PathParamName> names = new ArrayList<PathParamName>(strings.size());
        for(String n : strings){
            names.add(new PathParamName(n));
        }
        return names;
    }
    
    
    @Test
    public void returnsNullForNoMatch() {
        // given
        final PathPattern pattern = new RegexPathPattern(Pattern.compile("\\/dogs"));
        
        // when
        assertNull(pattern.match("/cats"));
        assertNotNull(pattern.match("/dogs"));
    }

    private <T> void assertListsEqual(List<T> expected, List<T> actual) {
        assertEquals(expected.size(), actual.size());
        for(int x=0;x<expected.size();x++){
            assertEquals(expected.get(x), actual.get(x));
        }
    }
}
