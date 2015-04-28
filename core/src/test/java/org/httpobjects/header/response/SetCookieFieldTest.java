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
package org.httpobjects.header.response;

import org.junit.Assert;

import org.junit.Test;

public class SetCookieFieldTest {

    @Test
    public void valuesMayBeQuoted(){

        // given
        String value = "foo=\"bar baz\"; path=\"/xyz\"; domain=\"xyz.com\"; Expires=\"Wed, 13-Jan-2021 22:23:01 GMT\"";
        // when
        SetCookieField c = SetCookieField.fromHeaderValue(value);

        // then
        Assert.assertEquals("foo", c.name);
        Assert.assertEquals("bar baz", c.value);
        Assert.assertEquals("/xyz", c.path);
        Assert.assertEquals("xyz.com", c.domain);
        Assert.assertEquals("Wed, 13-Jan-2021 22:23:01 GMT", c.expiration);
    }

    @Test
    public void attributeNamesAreCaseInsentitive(){
        
        // given
        String[] caseVariations = {"path", "PATH", "pAtH", "PaTh"};
        
        for(String pathAttributeName : caseVariations){
            
            // when
            SetCookieField c = SetCookieField.fromHeaderValue("foo=bar; " + pathAttributeName + "=/");
            
            // then
            Assert.assertEquals("foo", c.name);
            Assert.assertEquals("bar", c.value);
            Assert.assertEquals("/", c.path);
        }
    }
    
	@Test
	public void basicNameValueSetCookieField(){
		SetCookieField c = SetCookieField.fromHeaderValue("name=value");
		assertSame(
				new SetCookieField("name", "value", null),
				c
		);
		
		Assert.assertEquals("name=value", c.toString());
	}
	
	@Test
	public void basicSetCookieFieldWithDomain(){
		SetCookieField c = SetCookieField.fromHeaderValue("LSID=DQAAAK…Eaem_vYg; Domain=docs.foo.com");
		assertSame(
				new SetCookieField("LSID", "DQAAAK…Eaem_vYg", "docs.foo.com"),
				c
		);

		Assert.assertEquals("LSID=DQAAAK…Eaem_vYg; Domain=docs.foo.com;", c.toString());
	}
	

	@Test
	public void full(){
		SetCookieField c = SetCookieField.fromHeaderValue("SSID=Ap4P….GTEq; Domain=.foo.com; Path=/; Expires=Wed, 13-Jan-2021 22:23:01 GMT; Secure;");
		assertSame(
				new SetCookieField("SSID", "Ap4P….GTEq", ".foo.com", "/", "Wed, 13-Jan-2021 22:23:01 GMT", true),
				c
		);
		System.out.println(c);
		Assert.assertEquals("SSID=Ap4P….GTEq; Domain=.foo.com; Path=/; Expires=Wed, 13-Jan-2021 22:23:01 GMT; Secure;", c.toString());
		
	}
	private void assertSame(SetCookieField expected, SetCookieField actual){
		Assert.assertEquals(expected.name, actual.name);
		Assert.assertEquals(expected.value, actual.value);
		Assert.assertEquals(expected.domain, actual.domain);
		Assert.assertEquals(expected.path, actual.path);
		Assert.assertEquals(expected.expiration, actual.expiration);
		Assert.assertEquals(expected.secure, actual.secure);
	}
}
