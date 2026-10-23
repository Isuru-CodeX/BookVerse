package com.bookverse.command;

import com.bookverse.model.*;
import com.bookverse.service.*;
import com.bookverse.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/** Member: POST switches between the Standard and Premium plans. */
public class ChangePlanCommand implements Command {
    private final LibraryDelegate library = new LibraryDelegate();

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) {
        if (Web.isPost(request)) {
            Member member = (Member) request.getSession().getAttribute("member");
            try {
                Member m = library.changePlan(member.getEmail(), Web.param(request, "membershipType"));
                Flash.success(request.getSession(), "You are now on the " + library.plan(m.getMembershipType()).getLabel() + " plan.");
            } catch (LibraryException e) {
                Flash.error(request.getSession(), e.getMessage());
            }
        }
        return "redirect:/dashboard.do";
    }
}
