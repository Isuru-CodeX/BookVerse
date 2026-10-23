package com.bookverse.filter;

import javax.servlet.*;
import java.io.IOException;

/** Intercepting Filter #1: forces UTF-8 for request bodies and responses. */
public class EncodingFilter implements Filter {
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        req.setCharacterEncoding("UTF-8");
        res.setCharacterEncoding("UTF-8");
        chain.doFilter(req, res);
    }
}
