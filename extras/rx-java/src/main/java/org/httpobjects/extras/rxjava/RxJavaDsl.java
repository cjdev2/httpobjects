package org.httpobjects.extras.rxjava;

import org.httpobjects.Representation;
import org.httpobjects.Representation.Chunk;
import org.httpobjects.Request;
import org.httpobjects.Stream;
import org.httpobjects.Stream.Scanner;

import rx.Observable;
import rx.Subscriber;

public class RxJavaDsl {
	
	public static Representation RxBytes(String contentType, Observable<Byte> dataStream){
		return null;
	}
	public static Representation RxByteArrays(String contentType, Observable<byte[]> dataStream){
		return null;
	}

	public static Observable<Chunk> rxStream(Request r) {
		return rxStream(r.representation().bytes());
	}
	public static Observable<Chunk> rxStream(Stream<Chunk> stream) {
		return Observable.create(
		    new Observable.OnSubscribe<Chunk>() {
		        @Override
		        public void call(Subscriber<? super Chunk> sub) {
		    		stream.scan(new Scanner<Chunk>() {
		    			@Override
		    			public void collect(Chunk next) {
		    				if(next==null){
		    					sub.onCompleted();
		    				}else{
					            sub.onNext(next);
		    				}
		    			}
		    		});
		        }
		    }
		);
	}
	
}
