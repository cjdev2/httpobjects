package org.httpobjects.util;

import scala.concurrent.Future;

public class FutureUtil {
  public static <T> T waitFor(Future<T> f) {
    try {
      return FutureUtil.waitFor(f);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
