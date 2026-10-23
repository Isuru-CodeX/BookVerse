package com.bookverse.command;

import com.bookverse.bean.SearchCriteria;
import com.bookverse.service.LibraryDelegate;
import com.bookverse.util.Web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Search / filter / sort the catalogue. */
public class CatalogCommand implements Command {
    private final LibraryDelegate library = new LibraryDelegate();

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) {
        SearchCriteria criteria = new SearchCriteria();
        criteria.setKeyword(Web.param(request, "keyword"));
        criteria.setCategory(Web.param(request, "category"));
        criteria.setAvailableOnly("true".equals(Web.param(request, "availableOnly")));
        criteria.setSort(Web.param(request, "sort"));

        request.setAttribute("criteria", criteria);            // picked up by <jsp:useBean scope="request">
        request.setAttribute("books", library.search(criteria));
        request.setAttribute("categories", library.categories());
        return "catalog";
    }
}
