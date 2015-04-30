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

import akka.dispatch.ExecutionContexts;
import akka.dispatch.Futures;
import org.httpobjects.path.PathPattern;
import org.httpobjects.path.SimplePathPattern;
import scala.concurrent.ExecutionContext;
import scala.concurrent.ExecutionContextExecutor;
import scala.concurrent.Future;

public class HttpObject extends DSL {

    private final PathPattern pathPattern;
    private final Future<Response> defaultResponse;
    private final ExecutionContextExecutor ctx;
    private ExecutionContext executionContext;

    public HttpObject(PathPattern pathPattern, Response defaultResponse) {
        super();
        this.ctx = ExecutionContexts.global();
        this.pathPattern = pathPattern;
        this.defaultResponse = Futures.successful(defaultResponse);
    }
    
    public HttpObject(String pathPattern, Response defaultResponse) {
        this(new SimplePathPattern(pathPattern), defaultResponse);
    }

    public HttpObject(PathPattern pathPattern) {
        this(pathPattern, METHOD_NOT_ALLOWED());
    }

    public HttpObject(String pathPattern) {
        this(new SimplePathPattern(pathPattern));
    }

    public PathPattern pattern() {
        return pathPattern;
    }

    public Future<Response> delete(Request req){return defaultResponse;}
    public Future<Response> get(Request req){return defaultResponse;}
    public Future<Response> head(Request req){return defaultResponse;}
    public Future<Response> options(Request req){return defaultResponse;}
    public Future<Response> post(Request req){return defaultResponse;}
    public Future<Response> put(Request req){return defaultResponse;}
    public Future<Response> trace(Request req){return defaultResponse;}
    public Future<Response> patch(Request req){return defaultResponse;}

    public ExecutionContext getExecutionContext() {
        return executionContext;
    }

    public void setExecutionContext(ExecutionContext executionContext) {
        this.executionContext = executionContext;
    }
}
