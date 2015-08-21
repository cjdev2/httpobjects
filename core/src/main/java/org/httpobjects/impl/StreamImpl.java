package org.httpobjects.impl;

import java.io.IOException;

import org.httpobjects.Eventual;
import org.httpobjects.Stream;
import org.httpobjects.Representation.Chunk;

public class StreamImpl<T> implements Stream<T> {
    @Override
    public final <R> Stream<R> flatMap(org.httpobjects.Stream.Fn<T, Stream<R>> function) {
        throw notImplemented();
    }
    @Override
    public final <R> Stream<R> flatMapIterable(org.httpobjects.Stream.Fn<T, Iterable<R>> function) {
        throw notImplemented();
    }
    @Override
    public final <R> Stream<R> map(org.httpobjects.Stream.Fn<T, R> function) {
        throw notImplemented();
    }
    @Override
    public final <F> Eventual<F> read(org.httpobjects.Stream.Transformer<T, F> collector) {
        throw notImplemented();
    }
    @Override
    public final <F> F reduce(F initialValue, org.httpobjects.Stream.Fn<T, F> fold) {
        throw notImplemented();
    }

    @Override
    public void scan(org.httpobjects.Stream.Scanner<T> scanner) {
        throw notImplemented();
    }

    private RuntimeException notImplemented() {
        return new RuntimeException("NOT IMPLEMENTED");
    }
}
