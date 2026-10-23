package com.bookverse.util;

import javax.servlet.http.HttpSession;

/** One-shot messages stored in session scope and consumed by header.jsp. */
public final class Flash {
    private Flash() { }

    public static void set(HttpSession session, String type, String message) {
        session.setAttribute("flashType", type);
        session.setAttribute("flashMessage", message);
    }
    public static void success(HttpSession s, String m) { set(s, "success", m); }
    public static void error(HttpSession s, String m) { set(s, "error", m); }
    public static void info(HttpSession s, String m) { set(s, "info", m); }
}
