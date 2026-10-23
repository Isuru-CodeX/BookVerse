package com.bookverse.filter;

import com.bookverse.model.Member;
import com.bookverse.util.Flash;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Set;

/**
 * Intercepting Filter #3: access control for *.do commands.
 * Member commands need a signed-in user; staff commands also need the STAFF role.
 */
public class AuthFilter implements Filter {
    private static final Set<String> MEMBER_COMMANDS = Set.of("dashboard", "reserve", "return", "renew", "changePlan");
    /** Staff area: every command whose name starts with "admin". */
    private static boolean isStaffCommand(String command) { return command.startsWith("admin"); }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        String path = request.getServletPath();
        String command = path.substring(1, path.length() - ".do".length());

        boolean staffOnly = isStaffCommand(command);
        if (staffOnly || MEMBER_COMMANDS.contains(command)) {
            HttpSession session = request.getSession(false);
            Member member = session == null ? null : (Member) session.getAttribute("member");
            if (member == null) {
                Flash.info(request.getSession(), "Please sign in to continue.");
                String back = "GET".equals(request.getMethod())
                        ? path + (request.getQueryString() == null ? "" : "?" + request.getQueryString())
                        : "/dashboard.do";
                response.sendRedirect(request.getContextPath() + "/login.do?redirect="
                        + URLEncoder.encode(back, StandardCharsets.UTF_8));
                return;
            }
            if (!member.isActive()) {                       // suspended while signed in
                session.invalidate();
                Flash.error(request.getSession(), "This account is suspended. Please contact library staff.");
                response.sendRedirect(request.getContextPath() + "/login.do");
                return;
            }
            if (staffOnly && !member.isStaff()) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Library staff only.");
                return;
            }
        }
        chain.doFilter(req, res);
    }
}
