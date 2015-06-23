package org.httpobjects.scala

import org.httpobjects.test.MockRequest
import org.httpobjects.util.HttpObjectUtil
import org.httpobjects._
import DSL._
import ScalaDSL._
import org.scalatest.FunSuite

class ScalaDSLTest extends FunSuite {

  test("httpCompose path"){
    // given
    val testSubject = httpCompose("/foo")

    // when
    val rawPattern = testSubject.pattern().raw()

    // then
    assert(rawPattern === "/foo")
  }

  test("httpCompose returns METHOD_NOT_ALLOWED by default"){
    // given
    val testSubject = httpCompose("/foo")
    val in = new MockRequest(testSubject, "/foo")

    // when
    val responses = List(
      testSubject.get(in),
      testSubject.post(in),
      testSubject.put(in),
      testSubject.delete(in),
      testSubject.head(in),
      testSubject.options(in),
      testSubject.trace(in),
      testSubject.patch(in))

    // then
    assert(responses.map(_.get().code()).distinct === List(ResponseCode.METHOD_NOT_ALLOWED))
  }

  test("httpCompose supports GET"){
    // given
    val testSubject = httpCompose("/foo", GET=echo)
    val in = new MockRequest(testSubject, "/foo", Text("some content"))

    // when
    val out = testSubject.get(in);

    // then
    assert(HttpObjectUtil.toAscii(out.get.representation) === "/foo? some content")
  }
  test("httpCompose supports POST"){
    // given
    val testSubject = httpCompose("/foo", POST=echo)
    val in = new MockRequest(testSubject, "/foo", Text("some content"))

    // when
    val out = testSubject.post(in);

    // then
    assert(HttpObjectUtil.toAscii(out.get.representation) === "/foo? some content")
  }

  test("httpCompose supports PUT"){
    // given
    val testSubject = httpCompose("/foo", PUT=echo)
    val in = new MockRequest(testSubject, "/foo", Text("some content"))

    // when
    val out = testSubject.put(in);

    // then
    assert(HttpObjectUtil.toAscii(out.get.representation) === "/foo? some content")
  }


  test("httpCompose supports DELETE"){
    // given
    val testSubject = httpCompose("/foo", DELETE=echo)
    val in = new MockRequest(testSubject, "/foo", Text("some content"))

    // when
    val out = testSubject.delete(in);

    // then
    assert(HttpObjectUtil.toAscii(out.get.representation) === "/foo? some content")
  }


  test("httpCompose supports HEAD"){
    // given
    val testSubject = httpCompose("/foo", HEAD=echo)
    val in = new MockRequest(testSubject, "/foo", Text("some content"))

    // when
    val out = testSubject.head(in);

    // then
    assert(HttpObjectUtil.toAscii(out.get.representation) === "/foo? some content")
  }


  test("httpCompose supports OPTIONS"){
    // given
    val testSubject = httpCompose("/foo", OPTIONS=echo)
    val in = new MockRequest(testSubject, "/foo", Text("some content"))

    // when
    val out = testSubject.options(in);

    // then
    assert(HttpObjectUtil.toAscii(out.get.representation) === "/foo? some content")
  }

  test("httpCompose supports TRACE"){
    // given
    val testSubject = httpCompose("/foo", TRACE=echo)
    val in = new MockRequest(testSubject, "/foo", Text("some content"))

    // when
    val out = testSubject.trace(in);

    // then
    assert(HttpObjectUtil.toAscii(out.get.representation) === "/foo? some content")
  }

  test("httpCompose supports PATCH"){
    // given
    val testSubject = httpCompose("/foo", PATCH=echo)
    val in = new MockRequest(testSubject, "/foo", Text("some content"))

    // when
    val out = testSubject.patch(in);

    // then
    assert(HttpObjectUtil.toAscii(out.get.representation) === "/foo? some content")
  }


  private def echo:(Request)=>Eventual[Response] = {
    {r=>
      OK(Text(
        s"""${r.path}${r.query} ${HttpObjectUtil.toAscii(r.representation())}""".stripMargin))
    }
  }
}
