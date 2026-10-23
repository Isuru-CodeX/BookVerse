package com.bookverse.command;

import com.bookverse.service.LibraryDelegate;
import com.bookverse.util.Flash;
import com.bookverse.util.Web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Staff: post a library announcement. */
public class AnnounceCommand implements Command {
    private final LibraryDelegate library = new LibraryDelegate();

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) {
        if (Web.isPost(request)) {
            String title = Web.param(request, "title");
            String message = Web.param(request, "message");
            if (title.length() < 3 || title.length() > 80 || message.length() < 5 || message.length() > 300) {
                Flash.error(request.getSession(), "Announcements need a title (3-80 characters) and a message (5-300 characters).");
            } else {
                library.postAnnouncement(title, message);
                Flash.success(request.getSession(), "Announcement published on the home page.");
            }
        }
        return "redirect:/admin.do";
    }
}
