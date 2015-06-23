package org.httpobjects.scala

import org.httpobjects._
import DSL._
import ScalaDSL._
import org.httpobjects.netty.HttpobjectsNettySupport

import _root_.scala.concurrent.{Future, ExecutionContext}

object Demo {
  def main(args: Array[String]) {
    implicit val ec = ExecutionContext.global

    HttpobjectsNettySupport.serve(ec, 8080,
      httpCompose("/hi",
         GET = OK(Text("hi")),
         POST = Future {
           OK(Text("it worked"))
         }
      )
    )
  }

}
