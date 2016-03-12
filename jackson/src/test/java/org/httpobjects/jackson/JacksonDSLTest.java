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
package org.httpobjects.jackson;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.httpobjects.Representation;
import org.junit.Test;

public class JacksonDSLTest {

    @Test
    public void writesJsonRepresentationsOfSimpleBeans() {
        // given
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        RandomBean input = new RandomBean("Hello");

        // when
        JacksonDSL.JacksonJson(input).write(outputStream);

        // then
        assertEquals(outputStream.toString(), "{\"message\":\"Hello\"}");
    }
    
    @Test
    public void writesJsonRepresentationsOfStreamsOfStrings() {
        // given
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        List<String> strings = Arrays.asList("{\"message\":\"Hello\"}", "{\"message\":\"Goodbye\"}");

        // when
        JacksonDSL.JsonStream(strings).write(outputStream);

        // then
        assertEquals(outputStream.toString(), "[{\"message\":\"Hello\"},{\"message\":\"Goodbye\"}]");
    }

    @Test
    public void whatGoesInMustComeOut() throws IOException {
        // given
        RandomBean origBean = new RandomBean("Hello");
        Representation input = JacksonDSL.JacksonJson(origBean);

        // when
        RandomBean returnedBean = JacksonDSL.convertRepresentation(input).to(RandomBean.class);

        // then
        assertEquals(origBean, returnedBean);
    }


    public static class RandomBean {
        public String message;
        
        public RandomBean() {
        }
        
        RandomBean(String message) {
            this.message = message;
        }

        @Override
        public boolean equals(Object o) {
            return (o instanceof RandomBean) && ((RandomBean)o).toString().equals(this.toString());
        }
        
        @Override
        public String toString() {
            return message==null?null:message;
        }

        @Override
        public int hashCode() {
            return message != null ? message.hashCode() : 0;
        }
    }
}
