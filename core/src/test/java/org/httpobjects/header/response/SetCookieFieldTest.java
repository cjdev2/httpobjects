/**
 * <p>
 * Copyright (C) 2011, 2012 Commission Junction Inc.
 * </p>
 * <p>
 * This file is part of httpobjects.
 * </p>
 * <p>
 * httpobjects is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 * </p>
 * <p>
 * httpobjects is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 * </p>
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with httpobjects; see the file COPYING.  If not, write to the
 * Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA.
 * </p>
 * <p>
 * Linking this library statically or dynamically with other modules is
 * making a combined work based on this library.  Thus, the terms and
 * conditions of the GNU General Public License cover the whole
 * combination.
 * </p>
 * <p>
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
 * </p>
 */
package org.httpobjects.header.response;

import org.httpobjects.DateTimeRFC6265;
import org.junit.Test;

import static org.junit.Assert.*;

//https://tools.ietf.org/html/rfc6265
public class SetCookieFieldTest {



    @Test
    public void acceptsParsedExpirations() {

        // given
        DateTimeRFC6265 when = new DateTimeRFC6265("Wed, 13-Jan-2021 22:23:01 GMT");

        // when
        SetCookieField c = new SetCookieField("SSID", "Ap4P….GTEq", ".foo.com", "/", when, true, true);

        // then
        assertEquals("Wed, 13-Jan-2021 22:23:01 GMT", c.expiration);
    }


    @Test
    public void valuesMayBeQuoted() {

        // given
        String value = "foo=\"bar baz\"; path=\"/xyz\"; domain=\"xyz.com\"; Expires=\"Wed, 13-Jan-2021 22:23:01 GMT\"";
        // when
        SetCookieField c = SetCookieField.fromHeaderValue(value);

        // then
        assertEquals("foo", c.name);
        assertEquals("bar baz", c.value);
        assertEquals("/xyz", c.path);
        assertEquals("xyz.com", c.domain);
        assertEquals("Wed, 13-Jan-2021 22:23:01 GMT", c.expiration);
    }

    @Test
    public void attributeNamesAreCaseInsentitive() {

        // given
        String[] caseVariations = {"path", "PATH", "pAtH", "PaTh"};

        for (String pathAttributeName : caseVariations) {

            // when
            SetCookieField c = SetCookieField.fromHeaderValue("foo=bar; " + pathAttributeName + "=/");

            // then
            assertEquals("foo", c.name);
            assertEquals("bar", c.value);
            assertEquals("/", c.path);
        }
    }

    @Test
    public void flagsAreCaseInsensitive() {

        // given
        String[] httpCaseVariations = {"HTTPOnly", "httpONLY", "HttpOnly"};

        for (String httpOnlyFlag : httpCaseVariations) {

            // when
            SetCookieField c = SetCookieField.fromHeaderValue("foo=bar; " + httpOnlyFlag);

            // then
            assertEquals("foo", c.name);
            assertEquals("bar", c.value);
            assertEquals(true, c.httpOnly);
        }

        String[] secureCaseVariations = {"Secure", "secure", "SECURE"};

        for (String secureFlag : secureCaseVariations ) {

            // when
            SetCookieField c = SetCookieField.fromHeaderValue("foo=bar; " + secureFlag);

            // then
            assertEquals("foo", c.name);
            assertEquals("bar", c.value);
            assertEquals(true, c.secure);
        }
    }


    @Test
    public void attributeValuesAreOptional() {

        // given
        String value = "foo=\"bar baz\"; ThisAttributeHasNoValue";

        // when
        SetCookieField c = SetCookieField.fromHeaderValue(value);

        // then
        assertEquals("foo", c.name);
        assertEquals("bar baz", c.value);
    }


    @Test
    public void basicNameValueSetCookieField() {
        SetCookieField c = SetCookieField.fromHeaderValue("name=value");
        assertSetCookieFieldSame(
                new SetCookieField("name", "value", null),
                c
        );

        assertEquals("name=value", c.toString());
    }

    @Test
    public void basicSetCookieFieldWithDomain() {
        SetCookieField c = SetCookieField.fromHeaderValue("LSID=DQAAAK…Eaem_vYg; Domain=docs.foo.com");
        assertSetCookieFieldSame(
                new SetCookieField("LSID", "DQAAAK…Eaem_vYg", "docs.foo.com"),
                c
        );

        assertEquals("LSID=DQAAAK…Eaem_vYg; Domain=docs.foo.com;", c.toString());
    }


    @Test
    public void full() {
        SetCookieField c = SetCookieField.fromHeaderValue("SSID=Ap4P….GTEq; Domain=.foo.com; Path=/; Expires=Wed, 13-Jan-2021 22:23:01 GMT; Secure; HttpOnly;");
        boolean secure = true;
        boolean httpOnly = true;
        assertSetCookieFieldSame(
                new SetCookieField("SSID", "Ap4P….GTEq", ".foo.com", "/", "Wed, 13-Jan-2021 22:23:01 GMT", secure, httpOnly),
                c
        );
        assertEquals("SSID=Ap4P….GTEq; Domain=.foo.com; Path=/; Expires=Wed, 13-Jan-2021 22:23:01 GMT; Secure; HttpOnly;", c.toString());

    }

    @Test
    public void parseHttpOnlyTrue() {
        // given
        String headerValue = "name=value; HttpOnly;";

        // when
        SetCookieField setCookieField = SetCookieField.fromHeaderValue(headerValue);

        // then
        assertTrue(setCookieField.httpOnly);
    }

    @Test
    public void parseHttpOnlyFalse() {
        // given
        String headerValue = "name=value;";

        // when
        SetCookieField setCookieField = SetCookieField.fromHeaderValue(headerValue);

        // then
        assertNull(setCookieField.httpOnly);
    }

    @Test
    public void getterForHttpOnlyTrue() {
        // given
        SetCookieField cookieField = new SetCookieField(null, null, null, null, (String) null, null, Boolean.TRUE);

        // then
        assertTrue(cookieField.isHttpOnly());
    }

    @Test
    public void getterForHttpOnlyFalse() {
        // given
        SetCookieField cookieField = new SetCookieField(null, null, null, null, (String) null, null, Boolean.FALSE);

        // then
        assertFalse(cookieField.isHttpOnly());
    }

    @Test
    public void getterForHttpOnlyNull() {
        // given
        SetCookieField cookieField = new SetCookieField(null, null, null, null, (String) null, null, null);

        // then
        assertFalse(cookieField.isHttpOnly());
    }

    @Test
    public void getterForSecureTrue() {
        // given
        SetCookieField cookieField = new SetCookieField(null, null, null, null, (String) null, Boolean.TRUE, null);

        // then
        assertTrue(cookieField.isSecure());
    }

    @Test
    public void getterForSecureFalse() {
        // given
        SetCookieField cookieField = new SetCookieField(null, null, null, null, (String) null, Boolean.FALSE, null);

        // then
        assertFalse(cookieField.isSecure());
    }

    @Test
    public void getterForSecureNull() {
        // given
        SetCookieField cookieField = new SetCookieField(null, null, null, null, (String) null, null, null);

        // then
        assertFalse(cookieField.isSecure());
    }

    private void assertSetCookieFieldSame(SetCookieField expected, SetCookieField actual) {
        assertEquals(expected.name, actual.name);
        assertEquals(expected.value, actual.value);
        assertEquals(expected.domain, actual.domain);
        assertEquals(expected.path, actual.path);
        assertEquals(expected.expiration, actual.expiration);
        assertEquals(expected.secure, actual.secure);
        assertEquals(expected.httpOnly, actual.httpOnly);
    }
}
