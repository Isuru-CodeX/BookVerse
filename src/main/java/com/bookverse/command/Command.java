package com.bookverse.command;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Command pattern used by the Front Controller. Each implementation handles one URL (e.g. catalog.do).
 * Return value: a view name (resolved to /WEB-INF/views/NAME.jsp), "redirect:/path.do",
 * or null when the command already wrote the response.
 */
@FunctionalInterface
public interface Command {
    String execute(HttpServletRequest request, HttpServletResponse response) throws Exception;
}
