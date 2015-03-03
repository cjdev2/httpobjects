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

import static java.lang.Integer.valueOf;
import static org.junit.Assert.*;

import org.junit.Test;

public class ConnectionInfoTest {

    @Test
    public void whatGoesInMustComeOut(){
        // when
        ConnectionInfo testSubject = new ConnectionInfo("1.2.3.4", 8080, "4.3.2.1", 4433);

        // then
        assertEquals("4.3.2.1", testSubject.remoteAddress);
        assertEquals(valueOf(4433), testSubject.remotePort);
        assertEquals("1.2.3.4", testSubject.localAddress);
        assertEquals(valueOf(8080), testSubject.localPort);
    }


    @Test
    public void doesntAllowNullRemotePorts() {
        // given
        final Integer remotePort = null;

        // when
        Throwable t;
        try{
            new ConnectionInfo("1.2.3.4", 8080, "4.3.2.1", remotePort);
            t = null;
        }catch(Throwable e){
            t = e;
        }
        assertEquals(IllegalArgumentException.class.getName(), t.getClass().getName());
        assertEquals("Null not allowed", t.getMessage());
    }

    @Test
    public void doesntAllowNullRemoteAddresses() {
        // given
        final String remoteAddress = null;

        // when
        Throwable t;
        try{
            new ConnectionInfo("1.2.3.4", 8080, remoteAddress, 4433);
            t = null;
        }catch(Throwable e){
            t = e;
        }
        assertEquals(IllegalArgumentException.class.getName(), t.getClass().getName());
        assertEquals("Null not allowed", t.getMessage());
    }
    
    @Test
    public void doesntAllowNullLocalPorts() {
        // given
        final Integer localPort = null;

        // when
        Throwable t;
        try{
            new ConnectionInfo("1.2.3.4", localPort, "4.3.2.1", 4433);
            t = null;
        }catch(Throwable e){
            t = e;
        }
        assertEquals(IllegalArgumentException.class.getName(), t.getClass().getName());
        assertEquals("Null not allowed", t.getMessage());
    }

    @Test
    public void doesntAllowNullLocalAddresses() {
        // given
        final String localAddresses = null;

        // when
        Throwable t;
        try{
            new ConnectionInfo(localAddresses, 8080, "1.2.3.4", 4433);
            t = null;
        }catch(Throwable e){
            t = e;
        }
        assertEquals(IllegalArgumentException.class.getName(), t.getClass().getName());
        assertEquals("Null not allowed", t.getMessage());
    }

}
