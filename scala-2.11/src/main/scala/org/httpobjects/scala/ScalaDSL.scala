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
package org.httpobjects.scala

import org.httpobjects.outcome.{OutcomeHandler, OutcomeHandlerExecutor, Outcome}
import org.httpobjects.{Eventual => HFuture, _}


import _root_.scala.concurrent.duration.Duration
import _root_.scala.concurrent.{ExecutionContext, Promise, Future => SFuture}
import _root_.scala.util.{Failure, Success, Try}

object ScalaDSL {
  private type HttpMethodHandler = (Request)=>HFuture[Response]

  private def returnMethodNotAllowed:HttpMethodHandler = {r=> DSL.METHOD_NOT_ALLOWED}

  def httpCompose(
                   path:String,
                   GET:HttpMethodHandler = returnMethodNotAllowed,
                   HEAD:HttpMethodHandler = returnMethodNotAllowed,
                   OPTIONS:HttpMethodHandler = returnMethodNotAllowed,
                   POST:HttpMethodHandler = returnMethodNotAllowed,
                   PUT:HttpMethodHandler = returnMethodNotAllowed,
                   TRACE:HttpMethodHandler = returnMethodNotAllowed,
                   PATCH:HttpMethodHandler = returnMethodNotAllowed,
                   DELETE:HttpMethodHandler = returnMethodNotAllowed
                   ):HttpObject = {
    new HttpObject(path){
      override def get(req: Request) = GET(req)
      override def head(req: Request) = HEAD(req)
      override def options(req: Request) = OPTIONS(req)
      override def post(req: Request) = POST(req)
      override def put(req: Request) = PUT(req)
      override def trace(req: Request) = TRACE(req)
      override def patch(req: Request) = PATCH(req)
      override def delete(req: Request) = DELETE(req)
    }
  }

  implicit class RichFuture[T](val future: HFuture[T]) {

    def toScala(implicit ec:ExecutionContext):SFuture[T] = SFuture{future.get}

    def whenAvailable[R](fn:Try[T]=>R)(implicit ec:ExecutionContext): Unit = {
      val action = toAction(fn)
      future.onComplete(action, ec)
    }
  }

  /*
   * Conversions to http method handling functions
   */

  implicit def toHttpMethodHandler(response:Response):HttpMethodHandler = {_=>response}
  implicit def toHttpMethodHandler(response:HFuture[Response]):HttpMethodHandler = {_=>response}
  implicit def toHttpMethodHandler(response:SFuture[Response]):HttpMethodHandler = {_=>response}

  /*
   * Conversions to org.httpobject.Future
   */
  implicit def toHttpobjectsFuture(f:Promise[Response]):HFuture[Response] = toHttpobjectsFuture(f.future)
  implicit def toHttpobjectsFuture(f:SFuture[Response]):HFuture[Response] = new FutureWrapper[Response](f)

  /*
   * Conversions to/from scala.concurrent.ExceutionContext
   */
  implicit def asScalaExecutionContext(executor:OutcomeHandlerExecutor):ExecutionContext = new ActionExecutorWrapper(executor)
  implicit def asHttpobjectsActionExecutor(ec:ExecutionContext):OutcomeHandlerExecutor = {
    new OutcomeHandlerExecutor {
      override def execute[T](a: OutcomeHandler[T], resolved:Outcome[T]): Unit = {
        ec.execute(new Runnable {
          override def run(): Unit = a.exec(resolved)
        })
      }
    }
  }

  /*
   * Conversions to org.httpobject.Action
   */
  implicit def toAction[T](fn:Try[T]=>_):OutcomeHandler[T] = new OutcomeHandler[T](){
    def exec(resolved:Outcome[T]) = Try(resolved.get)
  }


}
