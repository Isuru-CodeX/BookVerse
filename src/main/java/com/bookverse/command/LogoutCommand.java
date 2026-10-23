package com.bookverse.command;

import com.bookverse.util.Flash;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/** Ends the session and returns to the home page. */
public class LogoutCommand implements Command {
    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) {
        HttpSession old = request.getSession(false);
        if (old != null) old.invalidate();
        Flash.info(request.getSession(), "You have been signed out.");
        return "redirect:/home.do";
    }
}
