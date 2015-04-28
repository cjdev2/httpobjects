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
package org.httpobjects.util;

import static org.httpobjects.DSL.OK;
import static org.httpobjects.DSL.Text;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.httpobjects.HttpObject;
import org.httpobjects.Request;
import org.httpobjects.Response;
import org.httpobjects.test.MockRequest;
import org.junit.Test;

public class HttpObjectUtilTest {

    class PatchTestingObject extends HttpObject {
        final Response response;
        final List<Request> requestsRecieved = new ArrayList<Request>();
        
        public PatchTestingObject(String pathPattern, Response response) {
            super(pathPattern);
            this.response = response;
        }

        @Override
        public Response patch(Request req) {
            requestsRecieved.add(req);
            return response;
        }
    }
    
    @Test
    public void pipesInputsAndOutputsToThePatchMethod() {
        // given
        final Response expectedResponse = OK(Text("Hello WOrld"));
        final PatchTestingObject o = new PatchTestingObject("/foo", expectedResponse);
        
        final Request input = new MockRequest(o, "/foo");
        
        // when
        Response result = HttpObjectUtil.invokeMethod(o, Method.PATCH, input);
        
        // then
        assertNotNull(result);
        assertTrue(expectedResponse == result);
        assertEquals(1, o.requestsRecieved.size());
        assertTrue(input == o.requestsRecieved.get(0));
        
    }
}
