package org.httpobjects.netty;

import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;

import org.httpobjects.HttpObject;
import org.httpobjects.Request;
import org.httpobjects.Response;
import org.httpobjects.netty.http.HttpServerPipelineFactory;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

public class HttpobjectsNettySupport {

      public static Channel serve(int port, List<HttpObject> objects) {
          // Configure the server.
          ServerBootstrap bootstrap = new ServerBootstrap(
                  new NioServerSocketChannelFactory(
                          Executors.newCachedThreadPool(),
                          Executors.newCachedThreadPool()));
  
          // Set up the event pipeline factory.
          bootstrap.setPipelineFactory(new HttpServerPipelineFactory(new NettyHttpobjectsRequestHandler(objects)));
  
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
          HttpobjectsNettySupport.serve(port, Arrays.<HttpObject>asList(
        		  new HttpObject("/") {
        				public Response get(Request req) {
        					return OK(Html("<html><body>Welcome.  Click <a href=\"/yo\">here</a> for a special message.</body></html>"));
        				}
        			},
            		new HttpObject("/yo") {
          				public Response get(Request req) {
          					return OK(Text("Hello world"));
          				}
          			}
              		));
      }
}
