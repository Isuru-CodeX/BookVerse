package com.bookverse.command;

import com.bookverse.model.Member;
import com.bookverse.model.Reservation;
import com.bookverse.service.LibraryDelegate;
import com.bookverse.service.LibraryException;
import com.bookverse.util.Flash;
import com.bookverse.util.Web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** POST: reserve a title for the signed-in member (Post/Redirect/Get). */
public class ReserveCommand implements Command {
    private final LibraryDelegate library = new LibraryDelegate();

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) {
        if (!Web.isPost(request)) return "redirect:/catalog.do";
        Member member = (Member) request.getSession().getAttribute("member");
        int bookId = Web.intParam(request, "bookId", -1);
        try {
            Reservation r = library.reserve(member.getEmail(), bookId);
            Flash.success(request.getSession(), "Reserved \"" + r.getBookTitle() + "\". Please return it by the due date.");
            return "redirect:/dashboard.do";
        } catch (LibraryException e) {
            Flash.error(request.getSession(), e.getMessage());
            return "redirect:/bookDetail.do?id=" + bookId;
        }
    }
}
