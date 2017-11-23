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

import org.httpobjects.path.Path;
import org.httpobjects.path.PathParamName;
import org.httpobjects.path.PathPattern;
import org.httpobjects.path.SimplePathPattern;
import org.httpobjects.util.HttpObjectUtil;
import org.httpobjects.util.Method;

import java.util.ArrayList;
import java.util.List;

public class HttpObject extends DSL{

    private final PathPattern pathPattern;
    private final Response defaultResponse;

    public HttpObject(PathPattern pathPattern, Response defaultResponse) {
        super();
        this.pathPattern = pathPattern;
        this.defaultResponse = defaultResponse;
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

    public Response delete(Request req){return defaultResponse;}
    public Response get(Request req){return defaultResponse;}
    public Response head(Request req){return defaultResponse;}
    public Response options(Request req){return defaultResponse;}
    public Response post(Request req){return defaultResponse;}
    public Response put(Request req){return defaultResponse;}
    public Response trace(Request req){return defaultResponse;}
    public Response patch(Request req){return defaultResponse;}

    public final HttpObject mask(HttpObject that) {
        return maskResources(this, that, NOT_FOUND());
    }

    public final HttpObject mask(HttpObject that, Representation notFound) {
        return maskResources(this, that, NOT_FOUND(notFound));
    }

    public interface Decorator<Id> {
        Id onRequest(Request request);
        void onResponse(Id id, Response response);
        void onError(Throwable error);
    }

    public final <Id> HttpObject decorate(Decorator<Id> decorator) {
        return decorateResource(this, decorator);
    }

    private static HttpObject maskResources(final HttpObject left,
                                            final HttpObject right,
                                            final Response notFound) {
        return new HttpObject(maskPatterns(left.pattern(), right.pattern())) {

            private HttpObject find(Request req) {
                if (left.pattern().matches(req.path().toString())) {
                    return left;
                } else if (right.pattern().matches(req.path().toString())) {
                    return right;
                } else {
                    return new HttpObject("", notFound);
                }
            }

            @Override
            public Response delete(Request req) {
                return find(req).delete(req);
            }

            @Override
            public Response get(Request req) {
                return find(req).get(req);
            }

            @Override
            public Response head(Request req) {
                return find(req).head(req);
            }

            @Override
            public Response options(Request req) {
                return find(req).options(req);
            }

            @Override
            public Response post(Request req) {
                return find(req).post(req);
            }

            @Override
            public Response put(Request req) {
                return find(req).put(req);
            }

            @Override
            public Response trace(Request req) {
                return find(req).trace(req);
            }

            @Override
            public Response patch(Request req) {
                return find(req).patch(req);
            }
        };
    }

    private static PathPattern maskPatterns(final PathPattern left,
                                            final PathPattern right) {
        return new PathPattern() {

            @Override
            public List<PathParamName> varNames() {
                List<PathParamName> result = new ArrayList<PathParamName>();
                result.addAll(left.varNames());
                result.addAll(right.varNames());
                return result;
            }

            @Override
            public boolean matches(String path) {
                return left.matches(path) || right.matches(path);
            }

            @Override
            public Path match(String path) {
                if (left.matches(path)) {
                    return left.match(path);
                } else {
                    return right.match(path);
                }
            }

            @Override
            public String raw() {
                return left.raw() + ":" + right.raw();
            }
        };
    }

    private static <Id> HttpObject decorateResource(final HttpObject resource,
                                                    final Decorator<Id> decorator) {
        return new HttpObject(resource.pattern()) {

            private Response dec(Method method, Request req) {
                try {
                    Id id = decorator.onRequest(req);
                    Response res = HttpObjectUtil.invokeMethod(resource, method, req);
                    decorator.onResponse(id, res);
                    return res;
                } catch (Throwable err) {
                    decorator.onError(err);
                    throw new RuntimeException(err);
                }
            }

            @Override
            public Response delete(Request req) {
                return dec(Method.DELETE, req);
            }

            @Override
            public Response get(Request req) {
                return dec(Method.GET, req);
            }

            @Override
            public Response head(Request req) {
                return dec(Method.HEAD, req);
            }

            @Override
            public Response options(Request req) {
                return dec(Method.OPTIONS, req);
            }

            @Override
            public Response post(Request req) {
                return dec(Method.POST, req);
            }

            @Override
            public Response put(Request req) {
                return dec(Method.PUT, req);
            }

            @Override
            public Response trace(Request req) {
                return dec(Method.TRACE, req);
            }

            @Override
            public Response patch(Request req) {
                return dec(Method.PATCH, req);
            }
        };
    }
}
