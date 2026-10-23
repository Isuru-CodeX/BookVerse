package com.bookverse.command;

import com.bookverse.model.*;
import com.bookverse.service.*;
import com.bookverse.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/** Staff: POST removes a title that has no active loans. */
public class AdminBookDeleteCommand implements Command {
    private final LibraryDelegate library = new LibraryDelegate();

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) {
        if (Web.isPost(request)) {
            try {
                Book b = library.deleteBook(Web.intParam(request, "id", -1));
                Flash.success(request.getSession(), "Removed \"" + b.getTitle() + "\" from the catalogue.");
            } catch (LibraryException e) {
                Flash.error(request.getSession(), e.getMessage());
            }
        }
        return "redirect:/adminBooks.do";
    }
}
