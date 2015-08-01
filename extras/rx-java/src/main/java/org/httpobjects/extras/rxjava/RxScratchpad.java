package org.httpobjects.extras.rxjava;

import static org.httpobjects.extras.rxjava.RxJavaDsl.RxByteArrays;
import static org.httpobjects.extras.rxjava.RxJavaDsl.rxStream;

import org.httpobjects.Eventual;
import org.httpobjects.HttpObject;
import org.httpobjects.Request;
import org.httpobjects.Response;

import rx.Observable;

public class RxScratchpad {
	public static void main(String[] args) {
		new HttpObject("/toString"){
			@Override
			public Eventual<Response> post(Request req) {
				Observable<byte[]> data = rxStream(req).map(chunk -> {
				  byte[] buffer = new byte[chunk.size()];
				  chunk.writeInto(buffer, 0);
				  return new String(buffer).toUpperCase().getBytes();
			    });
				
				return OK(RxByteArrays("text/plain", data));
			}
		};
	}
}
