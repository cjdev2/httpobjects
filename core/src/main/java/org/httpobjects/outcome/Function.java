package org.httpobjects.outcome;

public interface Function<Input, Output> {
  Output apply(Input in);
}
