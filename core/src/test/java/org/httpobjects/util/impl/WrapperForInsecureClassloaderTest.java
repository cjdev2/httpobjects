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
package org.httpobjects.util.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;
import junit.framework.AssertionFailedError;

import org.junit.Test;

public class WrapperForInsecureClassloaderTest {
    
    @Test
    public void passesTheDataThroughUnmolested() {
        // given
        ResourceLoader loader = new MockClasspathLoader("frog", new byte[]{0x1, 0x2, 0x3});
        
        WrapperForInsecureClassloader testSubject = new WrapperForInsecureClassloader(loader);
        
        // when
        InputStream result = testSubject.getResourceAsStream("frog");
        
        // then
        assertArrayEquals(new byte[]{0x1, 0x2, 0x3}, toByteArray(result));
    }
    
    @Test
    public void returnsNullWhenAskedForPathsThatAreNotSafeToUseWithInsecureClassloaders() {
        // given
        ResourceLoader loader = new MockClasspathLoader("frog/../../../foo", new byte[]{0x1, 0x2, 0x3});
        
        WrapperForInsecureClassloader testSubject = new WrapperForInsecureClassloader(loader);
        
        // when
        InputStream result = testSubject.getResourceAsStream("frog/../../../foo");
        
        // then
        Assert.assertNull("Expected null", result);
    }
    

    @SuppressWarnings("serial")
    private class MockClasspathLoader implements ResourceLoader {
        final Map<String, byte[]> resources;
        
        public MockClasspathLoader(final String name, final byte[] data) {
            this(new HashMap<String, byte[]>(){{
                put(name, data);
            }});
        }
        
        public MockClasspathLoader(Map<String, byte[]> resources) {
            super();
            this.resources = resources;
        }

        @Override
        public InputStream getResourceAsStream(String name) {
            final byte[] data = resources.get(name);
            return data==null?null:new ByteArrayInputStream(data);
        }
    }
    
    private static void assertArrayEquals(byte[] expected, byte[] actual){
        if(expected.length!=actual.length) throw new AssertionFailedError("expected " + expected.length + " bytes but received " + actual.length);
        for(int x=0;x<expected.length;x++){
            final byte byteExpected = expected[x];
            final byte actualByte = actual[x];
            if(byteExpected!=actualByte) throw new AssertionFailedError("expected " + Byte.toString(byteExpected) + " at byte #" + x + ", but found " + Byte.toString(actualByte));
        }
    }
    private static byte[] toByteArray(InputStream is){
        try {
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int numRead;
            while((numRead = is.read(buffer))!=-1){
                result.write(buffer, 0, numRead);
            }
            is.close();
            result.close();
            return result.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
