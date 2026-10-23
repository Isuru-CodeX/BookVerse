package com.bookverse.listener;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.util.concurrent.atomic.AtomicInteger;

/** Keeps the application-scope "activeSessions" counter up to date. */
public class SessionCountListener implements HttpSessionListener {
    @Override
    public void sessionCreated(HttpSessionEvent e) { adjust(e, 1); }

    @Override
    public void sessionDestroyed(HttpSessionEvent e) { adjust(e, -1); }

    private void adjust(HttpSessionEvent e, int delta) {
        AtomicInteger n = (AtomicInteger) e.getSession().getServletContext().getAttribute("activeSessions");
        if (n != null) n.addAndGet(delta);
    }
}
