/**
 * Copyright (C) 2011, 2012 Commission Junction Inc.
 * <p/>
 * This file is part of httpobjects.
 * <p/>
 * httpobjects is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 * <p/>
 * httpobjects is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License
 * along with httpobjects; see the file COPYING.  If not, write to the
 * Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA.
 * <p/>
 * Linking this library statically or dynamically with other modules is
 * making a combined work based on this library.  Thus, the terms and
 * conditions of the GNU General Public License cover the whole
 * combination.
 * <p/>
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

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.httpobjects.RepresentationUtil.*;
import static org.junit.Assert.assertThat;

public class RepresentationUtilTest {
    @Test
    public void stringRepresentation() {
        Representation representation = createRepresentationUtf8("text/plain", "Hello, world!");
        String actual = representationBodyAsUtf8(representation);
        assertThat(actual, is("Hello, world!"));
        assertThat(representation.contentType(), is("text/plain; charset=utf-8"));
    }

    @Test
    public void stringRepresentationWithCharset() {
        Representation representation = createRepresentation("text/plain", "Hello, world!", StandardCharset.ISO_8859_1);
        String actual = representationBodyAsString(representation, StandardCharset.ISO_8859_1);
        assertThat(actual, is("Hello, world!"));
        assertThat(representation.contentType(), is("text/plain; charset=iso-8859-1"));
    }
}