package org.httpobjects;

/**
 * An nonblocking, immutable stream of T.
 */
public interface Stream<T> {
	void scan(Stream.Scanner<T> scanner);
	<R> Stream<R> map(Stream.Fn<T, R> function);
	<F> F reduce(F initialValue, Stream.Fn<T, F> fold);
	<R> Stream<R> flatMap(Stream.Fn<T, Stream<R>> function);
	<R> Stream<R> flatMapIterable(Stream.Fn<T, Iterable<R>> function);
	
	<F> Eventual<F> read(Stream.Transformer<T, F> collector);
	

	public interface Scanner<T> {
		void collect(T next);
	}
	public interface Transformer<T, F> extends Stream.Scanner<T> {
		F finalResult();
	}
	public interface Fn<Input, Output> {
		Output apply(Input input);
	}
}