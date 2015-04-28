package org.httpobjects;

import akka.dispatch.Futures;
import org.httpobjects.header.HeaderField;
import scala.concurrent.ExecutionContext;
import scala.concurrent.Future;

import java.util.concurrent.Callable;

public class AsyncDSL {

  public static Future<Response> OK(ExecutionContext ctx, final Representation r, final HeaderField... header) {
    return Futures.future(new Callable<Response>() {
      @Override
      public Response call() throws Exception {
        return DSL.OK(r, header);
      }
    }, ctx);
  }
}
