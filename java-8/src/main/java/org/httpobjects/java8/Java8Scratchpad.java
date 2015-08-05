package org.httpobjects.java8;

import static org.httpobjects.DSL.Text;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;

import org.httpobjects.DSL;
import org.httpobjects.Eventual;
import org.httpobjects.HttpObject;
import org.httpobjects.Representation;
import org.httpobjects.Representation.Chunk;
import org.httpobjects.Request;
import org.httpobjects.Response;

public class Java8Scratchpad {
	
	private static String utf8String(byte[] data){
		try {
			return new String(data, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}
	private static byte[] utf8Bytes(String text){
		try {
			return text.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static void main(String[] args) throws Exception {
		new HttpObject("/toUppercase"){

			@Override
			public Eventual<Response> post(Request req) {
				
				return OK(Bytes("text/plain", 
						req.representation()
						   .bytes()
						   .map(chunk -> utf8String(chunk.toNewArray()))
						   .map(text -> utf8Bytes(text.toUpperCase()))));
			}
			
		};
		
		
		Representation r = Text("hello world");
		
		/*
		 * Goals
		 *   - allow the response to be collected asychronously with java6
		 *   - allow natural interop with java8 & scala
		 *   - immutable, if possible
		 */
		WritableByteChannel cout = null;
		r.bytes().scan(chunk -> chunk.writeInto(cout));

		OutputStream out = new ByteArrayOutputStream();
		r.bytes().scan(chunk -> chunk.writeInto(out));
		

		Eventual<Java8Scratchpad> result = r.bytes().read(asJackson(Java8Scratchpad.class));
		
		String textContent = r.bytes().read(asString("UTF-8")).get();

		r.bytes()
		 .read(asUtf8())
		 .onComplete(
           text->System.out.println(text), 
           DSL.syncronousExecutor());
		
		/*
		 * Don't want to re-write all of java8's functional stuff
		 * Don't want to write a complete implementation of functional stuff in general
		 * 
		 * The problem:
		 *   Given that
		 *    the target languages are java6, java7, java8, scala, kotlin clojure
		 *    and
		 *    the target languages don't have a common functional foundation
		 *       - no common Function interface
		 *       - no common Monad interface
		 *    the target languages don't have a common async foundation
		 *       - no common Promise/Future interface
		 *       - no common 'executor' interface
		 *    and
		 *    we want to use functional/async patterns in the 1.x API
		 *   Therefore:
		 *    we cannot proceed without either 
		 *     dropping the functional/async patterns
		 *     or
		 *     implementing at least a minimal, httpobjects-specific version of the patterns
		 *     
		 *  Mitigating factors:
		 *    - scala's implicit conversions
		 *    - clojure's lack of typesafety
		 *    - java8's function -> class coercion
		 *    
		 *    
		 *  What would go in our 'stream' interface?
		 *    - foreach
		 *    - reduce?
		 *    - map?
		 *    
		 *  Guiding principle:
		 *    don't just support java6, support it /well/!
		 *      - e.g. lots of helper methods
		 *      - e.g. classes in places where function composition might have otherwise been preferrable
		 *      
		 *  Observation: There are really 2 different use cases for a ByteStream:
		 *    - Accumulating them, then consuming the aggregate, possibly transformed, result
		 *    - Processing them as a stream
		 *        - e.g. one transforming it one byte at a time
		 *        - e.g. buffering & re-grouping them, then consuming as a stream of packets
		 *        - e.g. using something like rxjava to do reactive stream processing
		 *  
		 *  Q: Do we want to really be the full support for building protocol servers?
		 *    - nope, just make it easy to integrate with other stream processing APIs
		 */
		
//		r.bytes()
//		  .reduce(new ByteArrayOutputStream(), (chunk, accum) -> chunk.write(accum))
//		  .read(jackson(Test.class));
		
//		r.bytes()
//		 .group()
//		 .
		
		// rx-java
//		rxStream(r.bytes()).map(chunk -> chunk.size()).mkString(",");
		
		// java8
//		toStream(r.bytes()).map()
		
		// scala
		
//		r.bytes().
		
		
//		r.bytes()
//		 .read(asString("UTF-8"))
//		 .then(text -> DSL.now(text.toUpperCase()));
		
//		r.collect(new ByteArrayOutputStream(), (chunk, out) -> chunk.writeInfo(out)).map(Jackson.read(out));
		
//		Collections.EMPTY_LIST.stream().collect(collector)
	}
	

	static Transformer<Chunk, String> asUtf8(){
		return null;
	}

	static Transformer<Chunk, String> asString(String charset){
		return null;
	}

	
	static Transformer<Chunk, String> string(Charset charset){
		return null;
	}
	
	static <T> Transformer<Chunk, T> asJackson(Class<? extends T> clazz){
		return null;
	}
	
	static class JacksonAccumulator<T> implements Transformer<Chunk, T> {
		JacksonAccumulator(Class<? extends T> clazz){
			
		}

		@Override
		public void collect(Chunk nextChunk) {
			throw new RuntimeException("NOT IMLEMENTED");
		}

		@Override
		public T finalResult() {
			throw new RuntimeException("NOT IMLEMENTED");
		}
		
	}
}
