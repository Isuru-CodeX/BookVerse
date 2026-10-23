package com.bookverse.util;

import javax.servlet.http.HttpServletRequest;

/** Small request helpers shared by the commands. */
public final class Web {
    private Web() { }

    /** Trimmed parameter value, never null. */
    public static String param(HttpServletRequest r, String name) {
        String v = r.getParameter(name);
        return v == null ? "" : v.trim();
    }

    public static int intParam(HttpServletRequest r, String name, int fallback) {
        try { return Integer.parseInt(param(r, name)); } catch (NumberFormatException e) { return fallback; }
    }

    public static double doubleParam(HttpServletRequest r, String name, double fallback) {
        try { return Double.parseDouble(param(r, name)); } catch (NumberFormatException e) { return fallback; }
    }

    public static boolean isPost(HttpServletRequest r) { return "POST".equalsIgnoreCase(r.getMethod()); }
}
