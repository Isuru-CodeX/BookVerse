package com.bookverse.command;

import com.bookverse.model.*;
import com.bookverse.service.*;
import com.bookverse.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/** Member: POST renews a loan within the plan's renewal allowance. */
public class RenewCommand implements Command {
    private final LibraryDelegate library = new LibraryDelegate();

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) {
        if (Web.isPost(request)) {
            Member member = (Member) request.getSession().getAttribute("member");
            try {
                Reservation r = library.renew(member.getEmail(), Web.intParam(request, "reservationId", -1));
                Flash.success(request.getSession(), "Renewed \"" + r.getBookTitle() + "\". New due date applied.");
            } catch (LibraryException e) {
                Flash.error(request.getSession(), e.getMessage());
            }
        }
        return "redirect:/dashboard.do";
    }
}
