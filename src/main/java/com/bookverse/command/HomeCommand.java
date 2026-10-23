package com.bookverse.command;

import com.bookverse.service.LibraryDelegate;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

/** Landing page: featured titles, announcements, last-visit cookie and the visit counter. */
public class HomeCommand implements Command {
    public static final String VISIT_COOKIE = "bv_lastVisit";
    private final LibraryDelegate library = new LibraryDelegate();

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) {
        // read previous visit from the cookie (value is untrusted, so parse defensively)
        if (request.getCookies() != null) {
            for (Cookie c : request.getCookies()) {
                if (VISIT_COOKIE.equals(c.getName())) {
                    try { request.setAttribute("lastVisit", new Date(Long.parseLong(c.getValue()))); }
                    catch (NumberFormatException ignored) { /* ignore tampered cookie */ }
                }
            }
        }
        Cookie visit = new Cookie(VISIT_COOKIE, String.valueOf(System.currentTimeMillis()));
        visit.setMaxAge(60 * 60 * 24 * 30);
        visit.setHttpOnly(true);
        visit.setPath(request.getContextPath().isEmpty() ? "/" : request.getContextPath());
        response.addCookie(visit);

        // application-scope counter maintained by AppContextListener
        ServletContext ctx = request.getServletContext();
        AtomicInteger counter = (AtomicInteger) ctx.getAttribute("visitCounter");
        if (counter != null) counter.incrementAndGet();

        request.setAttribute("featured", library.topRated(4));
        request.setAttribute("newest", library.newestAdditions(4));
        request.setAttribute("announcements", library.announcements(3));
        request.setAttribute("stats", library.stats());
        return "home";
    }
}
