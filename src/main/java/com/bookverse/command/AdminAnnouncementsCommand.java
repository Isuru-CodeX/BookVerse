package com.bookverse.command;

import com.bookverse.model.*;
import com.bookverse.service.*;
import com.bookverse.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/** Staff: list, publish (action=add) and delete (action=delete) announcements. */
public class AdminAnnouncementsCommand implements Command {
    private final LibraryDelegate library = new LibraryDelegate();

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) {
        if (Web.isPost(request)) {
            if ("delete".equals(Web.param(request, "action"))) {
                boolean ok = library.deleteAnnouncement(Web.intParam(request, "id", -1));
                if (ok) Flash.success(request.getSession(), "Announcement deleted.");
                else Flash.error(request.getSession(), "That announcement no longer exists.");
            } else {
                String title = Web.param(request, "title");
                String message = Web.param(request, "message");
                if (title.length() < 3 || title.length() > 80 || message.length() < 5 || message.length() > 300) {
                    Flash.error(request.getSession(), "Announcements need a title (3-80 characters) and a message (5-300 characters).");
                } else {
                    library.postAnnouncement(title, message);
                    Flash.success(request.getSession(), "Announcement published on the home page.");
                }
            }
            return "redirect:/adminAnnouncements.do";
        }
        request.setAttribute("announcements", library.allAnnouncements());
        return "adminAnnouncements";
    }
}
