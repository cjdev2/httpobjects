package org.httpobjects.demos.scala

import org.httpobjects.{Request, HttpObject}
import org.httpobjects.netty.HttpobjectsNettySupport
import org.httpobjects.DSL._
import org.httpobjects.scala.ScalaDSL._

import scala.concurrent.ExecutionContext

object Demo extends App {

  val threads = ExecutionContext.global

  HttpobjectsNettySupport.serve(threads, 8080,
      new HttpObject("/"){
        override def get(req:Request) = OK(Text("hello world"))
      }
  )
}