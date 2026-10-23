package com.bookverse.command;

import com.bookverse.model.*;
import com.bookverse.service.*;
import com.bookverse.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/** Staff: POST action=return|extend on one loan. */
public class AdminLoanActionCommand implements Command {
    private final LibraryDelegate library = new LibraryDelegate();

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) {
        String status = Web.param(request, "status");
        if (Web.isPost(request)) {
            int id = Web.intParam(request, "id", -1);
            try {
                if ("return".equals(Web.param(request, "action"))) {
                    Reservation r = library.adminReturn(id);
                    Flash.success(request.getSession(), "Marked \"" + r.getBookTitle() + "\" as returned.");
                } else {
                    Reservation r = library.extendLoan(id, 7);
                    Flash.success(request.getSession(), "Extended \"" + r.getBookTitle() + "\" by 7 days.");
                }
            } catch (LibraryException e) {
                Flash.error(request.getSession(), e.getMessage());
            }
        }
        return "redirect:/adminLoans.do" + (status.matches("[a-z]{3,10}") ? "?status=" + status : "");
    }
}
