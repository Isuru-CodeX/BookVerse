package com.bookverse.command;

import com.bookverse.model.Member;
import com.bookverse.model.Reservation;
import com.bookverse.service.LibraryDelegate;
import com.bookverse.service.LibraryException;
import com.bookverse.util.Flash;
import com.bookverse.util.Web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** POST: return a borrowed title. */
public class ReturnBookCommand implements Command {
    private final LibraryDelegate library = new LibraryDelegate();

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) {
        if (!Web.isPost(request)) return "redirect:/dashboard.do";
        Member member = (Member) request.getSession().getAttribute("member");
        try {
            Reservation r = library.returnBook(member.getEmail(), Web.intParam(request, "reservationId", -1));
            Flash.success(request.getSession(), "\"" + r.getBookTitle() + "\" was returned. Thank you.");
        } catch (LibraryException e) {
            Flash.error(request.getSession(), e.getMessage());
        }
        return "redirect:/dashboard.do";
    }
}
