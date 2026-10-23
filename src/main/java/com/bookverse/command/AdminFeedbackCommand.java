package com.bookverse.command;

import com.bookverse.model.*;
import com.bookverse.service.*;
import com.bookverse.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/** Staff: read and delete member feedback. */
public class AdminFeedbackCommand implements Command {
    private final LibraryDelegate library = new LibraryDelegate();

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) {
        if (Web.isPost(request)) {
            boolean ok = library.deleteFeedback(Web.intParam(request, "id", -1));
            if (ok) Flash.success(request.getSession(), "Feedback deleted.");
            else Flash.error(request.getSession(), "That feedback no longer exists.");
            return "redirect:/adminFeedback.do";
        }
        request.setAttribute("feedbackList", library.allFeedback());
        request.setAttribute("report", library.report());
        return "adminFeedback";
    }
}
