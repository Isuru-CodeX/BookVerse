package com.bookverse.command;

import com.bookverse.model.*;
import com.bookverse.service.*;
import com.bookverse.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/** Staff: searchable list of all titles with edit/delete actions. */
public class AdminBooksCommand implements Command {
    private final LibraryDelegate library = new LibraryDelegate();

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) {
        com.bookverse.bean.SearchCriteria c = new com.bookverse.bean.SearchCriteria();
        c.setKeyword(Web.param(request, "keyword"));
        c.setCategory(Web.param(request, "category"));
        request.setAttribute("criteria", c);
        request.setAttribute("books", library.search(c));
        request.setAttribute("categories", library.categories());
        return "adminBooks";
    }
}
