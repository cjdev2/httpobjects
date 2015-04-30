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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.httpobjects.HttpObject;
import org.httpobjects.Representation;
import org.httpobjects.Request;
import org.httpobjects.Response;
import scala.concurrent.Future;

public class HttpObjectUtil {

    public static Future<Response> invokeMethod(HttpObject object, final Method m, final Request input) {
        final Future<Response> output;
        switch(m){
        case GET: 
            output = object.get(input);
            break;
        case DELETE:
            output = object.delete(input);
            break;
        case POST:
            output = object.post(input);
            break;
        case PUT:
            output = object.put(input);
            break;
        case PATCH:
            output = object.patch(input);
            break;
        case HEAD:
            output = object.head(input);
            break;
        default:
            output = HttpObject.NOT_FOUND().toFuture();
        }
        return output;
    }

    public static byte[] toByteArray(Representation r){
        try {
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            r.write(out);
            out.close();
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String toAscii(Representation r){
        try {
            return new String(toByteArray(r), "ascii");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

    }
}
