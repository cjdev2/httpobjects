package org.httpobjects.netty;

import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;

import org.httpobjects.*;
import org.httpobjects.netty.http.ByteAccumulatorFactory;
import org.httpobjects.netty.http.HttpServerPipelineFactory;
import org.httpobjects.netty.http.InMemoryByteAccumulatorFactory;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

public class HttpobjectsNettySupport {

          
      public static Channel serve(ActionExecutor context, int port, List<HttpObject> objects) {
          ByteAccumulatorFactory buffers = new InMemoryByteAccumulatorFactory();
          return serve(context, port, objects, buffers);
      }

      public static Channel serve(ActionExecutor context, int port, List<HttpObject> objects, ByteAccumulatorFactory buffers) {
          // Configure the server.
          ServerBootstrap bootstrap = new ServerBootstrap(
                  new NioServerSocketChannelFactory(
                          Executors.newCachedThreadPool(),
                          Executors.newCachedThreadPool()));
  
          // Set up the event pipeline factory.
          bootstrap.setPipelineFactory(new HttpServerPipelineFactory(context, new NettyHttpobjectsRequestHandler(objects), buffers));
  
          // Bind and start to accept incoming connections.
          return bootstrap.bind(new InetSocketAddress(port));
      }
  
      public static void main(String[] args) {
          int port;
          if (args.length > 0) {
              port = Integer.parseInt(args[0]);
          } else {
              port = 8080;
          }

          ActionExecutor executor = DSL.syncronousExecutor();

          HttpobjectsNettySupport.serve(executor, port, Arrays.<HttpObject>asList(
        		  new HttpObject("/") {
        				public Eventual<Response> get(Request req) {
        					return OK(Html("<html><body>Welcome.  Click <a href=\"/yo\">here</a> for a special message.</body></html>")).toFuture();
        				}
        			},
            		new HttpObject("/yo") {
          				public Eventual<Response> get(Request req) {
          					return OK(Text("Hello world")).toFuture();
          				}
          			}
              		));
      }
}
