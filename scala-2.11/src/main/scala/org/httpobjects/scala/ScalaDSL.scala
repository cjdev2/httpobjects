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

import org.httpobjects.{Eventual=>HFuture, Response, DSL, ActionExecutor, Action}


import scala.concurrent.duration.Duration
import scala.concurrent.{ExecutionContext, Promise, Future => SFuture}
import scala.util.{Failure, Success, Try}

object ScalaDSL {
  implicit class RichFuture[T](val future: HFuture[T]) {

    def toScala(implicit ec:ExecutionContext):SFuture[T] = SFuture{future.get}

    def whenAvailable[R](fn:Try[T]=>R)(implicit ec:ExecutionContext): Unit = {
      val action = toAction(fn)
      future.onComplete(action, ec)
    }
  }

  /*
   * Conversions to org.httpobject.Future
   */
  implicit def toHttpobjectsFuture(response:Response):HFuture[Response] = DSL.now(response)
  implicit def toHttpobjectsFuture(f:Promise[Response]):HFuture[Response] = toHttpobjectsFuture(f.future)
  implicit def toHttpobjectsFuture(f:SFuture[Response]):HFuture[Response] = new FutureWrapper[Response](f)

  /*
   * Conversions to/from scala.concurrent.ExceutionContext
   */
  implicit def asScalaExecutionContext(executor:ActionExecutor):ExecutionContext = new ActionExecutorWrapper(executor)
  implicit def asHttpobjectsActionExecutor(ec:ExecutionContext):ActionExecutor = {
    new ActionExecutor {
      override def execute[T](a: Action[T], value: T, err:Throwable): Unit = {
        ec.execute(new Runnable {
          override def run(): Unit = a.exec(value, err)
        })
      }
    }
  }

  /*
   * Conversions to org.httpobject.Action
   */
  implicit def toAction[T](fn:Try[T]=>_):Action[T] = new Action[T](){
    def exec(value: T, err:Throwable) = {
      val valueOrErr = (value, err) match {
        case (value, null) => Success(value)
        case (null, err) => Failure(err)
      }
      fn(valueOrErr)
    }
  }


}
