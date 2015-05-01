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

import org.httpobjects.outcome.OutcomeHandlerExecutor;
import org.httpobjects.outcome.Outcome;
import org.httpobjects.outcome.OutcomeHandler;

/**
 * Lightweight implementation of a Future.
 *
 *   Q: Yet another Future implementation?
 *   A: Yes, but there's a good reason!
 *     - given our design goal of zero external dependencies in the core api, we can't use some other library
 *     - given our design goal of jdk6 compatibility, we are restricted to what's avail in jdk6
 *     - there's nothing suitable in jdk6
 *
 *   Q: Why not use java.util.concurrent.Future?
 *   A: java.util.concurrent.Future doesn't have a callback mechanism
 *
 * http://en.wikipedia.org/wiki/Futures_and_promises
 */
public interface Eventual<V> extends Outcome<V> {
    /**
     * Executes the callback upon resolution of this Future.  The callback is executed using the specified executor.
     */
    void onComplete(OutcomeHandler<V> callback, OutcomeHandlerExecutor executor);

    /**
     * Immediately returns the result of this future, if currently resolved, otherwise null.
     * Throws an exception if the Future resolved with an exception
     */
    V getOrNull();

    /**
     * Blocks the current thread until this Future is resolved, returning the result.
     * Throws an exception if the Future resolved with an exception
     */
    V get();

}