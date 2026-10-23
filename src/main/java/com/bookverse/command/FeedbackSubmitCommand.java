package com.bookverse.command;

import com.bookverse.bean.FeedbackBean;
import com.bookverse.service.LibraryDelegate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Receives the validated "fb" bean that feedback.jsp placed in request scope and then
 * forwarded here with &lt;jsp:forward&gt; (the JSP does view-side validation, this command persists).
 */
public class FeedbackSubmitCommand implements Command {
    private final LibraryDelegate library = new LibraryDelegate();

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) {
        Object bean = request.getAttribute("fb");
        if (!(bean instanceof FeedbackBean) || !((FeedbackBean) bean).isValid()) {
            return "redirect:/feedback.jsp";
        }
        library.saveFeedback((FeedbackBean) bean);
        return "feedbackThanks";
    }
}
