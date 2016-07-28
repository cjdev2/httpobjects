package org.httpobjects.jetty;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;

public class JettyServerDelegate implements JettyServerContract {
    private final Server delegate;

    public JettyServerDelegate(Server delegate) {
        this.delegate = delegate;
    }

    @Override
    public void start() {
        try {
            delegate.start();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public void stop() {
        try {
            delegate.stop();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public void join() {
        try {
            delegate.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public void setHandler(Handler handler) {
        delegate.setHandler(handler);
    }
}
