package com.bookverse.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

/** Intercepting Filter #2: logs each request with its processing time and counts total requests. */
public class LoggingFilter implements Filter {
    private ServletContext context;
    private final AtomicLong requests = new AtomicLong();

    @Override
    public void init(FilterConfig config) { this.context = config.getServletContext(); }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        long start = System.nanoTime();
        try {
            chain.doFilter(req, res);
        } finally {
            HttpServletRequest r = (HttpServletRequest) req;
            long ms = (System.nanoTime() - start) / 1_000_000;
            context.setAttribute("requestCount", requests.incrementAndGet());
            context.log("[BookVerse] " + r.getMethod() + " " + r.getRequestURI() + " -> " + ms + " ms");
        }
    }
}
