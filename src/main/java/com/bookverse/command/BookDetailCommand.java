package com.bookverse.command;

import com.bookverse.model.Book;
import com.bookverse.service.LibraryDelegate;
import com.bookverse.util.Web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Show one title with related items. */
public class BookDetailCommand implements Command {
    private final LibraryDelegate library = new LibraryDelegate();

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
        int id = Web.intParam(request, "id", -1);
        Book book = library.findBook(id);
        if (book == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "That title could not be found.");
            return null;
        }
        com.bookverse.model.Member member = (com.bookverse.model.Member) request.getSession().getAttribute("member");
        request.setAttribute("book", book);
        request.setAttribute("loanDays", library.loanDaysFor(member, book));
        request.setAttribute("plan", library.plan(member == null ? "STANDARD" : member.getMembershipType()));
        request.setAttribute("related", library.relatedTo(book, 3));
        return "bookDetail";
    }
}
