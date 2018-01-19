package org.httpobjects.jetty;

import org.eclipse.jetty.server.Handler;

public interface JettyServerContract {
    void start();

    void stop();

    void join();

    void setHandler(Handler handler);
}
