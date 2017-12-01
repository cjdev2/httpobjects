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
package org.httpobjects.test;

import static org.httpobjects.DSL.Bytes;

import org.httpobjects.ConnectionInfo;
import org.httpobjects.HttpObject;
import org.httpobjects.Query;
import org.httpobjects.Representation;
import org.httpobjects.Request;
import org.httpobjects.header.HeaderField;
import org.httpobjects.header.request.RequestHeader;
import org.httpobjects.path.Path;
import org.httpobjects.util.Method;

public class MockRequest implements Request {
	private final Path path;
	private final Representation representation;
	private final Query query;
    private final RequestHeader header;
    private final ConnectionInfo connectionInfo;
    private final Method method;

    public MockRequest(ConnectionInfo connectionInfo, HttpObject object, String path, Query query, Representation representation, Method method, HeaderField... fields) {
        super();
        assertNoQueryInPath(path);
        this.method = method;
        this.connectionInfo = connectionInfo;
        this.representation = representation;
        this.query = query;
        this.path = object.pattern().match(path);
        if (this.path == null) {
            throw new RuntimeException(object.pattern().raw() + " does not match " + path);
        }
        this.header = new RequestHeader(fields);
    }

    public MockRequest(ConnectionInfo connectionInfo, HttpObject object, String path, Query query, Representation representation, HeaderField... fields) {
        this(connectionInfo, object, path, query, representation, Method.GET, fields);
    }

    public MockRequest(HttpObject object, String path, Query query, Representation representation, HeaderField ... fields) {
        this(new ConnectionInfo("1.2.3.4", 8080, "4.3.2.1", 4332), object, path, query, representation, fields);
    }

	public MockRequest(HttpObject object, String path, Query query, HeaderField ... fields) {
		this(object, path, query, nullRepresentation(), fields);
	}

	public MockRequest(HttpObject object, String path, HeaderField ... fields) {
        this(object, path, new Query(null), nullRepresentation(), fields);
	}

	public MockRequest(HttpObject object, String path, Representation representation, HeaderField ... fields) {
        this(object, path, new Query(""), representation, fields);
	}

	@Override
	public ConnectionInfo connectionInfo() {
	    return connectionInfo;
	}

	@Override
	public RequestHeader header() {
		return header;
	}
	@Override
	public Path path() {
		return path;
	}

	@Override
	public boolean hasRepresentation() {
		return representation!=null;
	}

	@Override
	public Representation representation() {
		return representation;
	}

	@Override
	public Query query() {
		return query;
	}

	@Override
    public Method method() {
        return method;
    }

	@Override
	public Request immutableCopy() {
		return this;
	}


    private static Representation nullRepresentation(){
        return Bytes(null, new byte[]{});
    }

    private static void assertNoQueryInPath(String path){
        if(path.indexOf('?')!=-1)
            throw new RuntimeException("The query is in the path, but I wasn't expecting that; try using a constructor that takes a " + Query.class.getName() + " argument.");
    }
}
