package com.bookverse.command;

import com.bookverse.model.Book;
import com.bookverse.service.LibraryDelegate;
import com.bookverse.util.Flash;
import com.bookverse.util.Web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Map;

/** Staff: add a new title to the catalogue. */
public class AddBookCommand implements Command {
    private final LibraryDelegate library = new LibraryDelegate();

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) {
        request.setAttribute("categories", library.categories());
        if (!Web.isPost(request)) {
            Book blank = new Book();
            blank.setYear(Calendar.getInstance().get(Calendar.YEAR));
            blank.setTotalCopies(1);
            request.setAttribute("book", blank);
            return "bookForm";
        }

        Map<String, String> errors = new LinkedHashMap<>();
        Book book = new Book();
        book.setTitle(Web.param(request, "title"));
        book.setAuthor(Web.param(request, "author"));
        book.setCategory(Web.param(request, "category"));
        book.setIsbn(Web.param(request, "isbn"));
        book.setDescription(Web.param(request, "description"));
        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
        book.setYear(Web.intParam(request, "year", 0));
        book.setTotalCopies(Web.intParam(request, "totalCopies", 0));

        if (book.getTitle().length() < 2 || book.getTitle().length() > 120) errors.put("title", "Enter a title (2-120 characters).");
        if (book.getAuthor().length() < 2 || book.getAuthor().length() > 120) errors.put("author", "Enter the author or publisher.");
        if (book.getCategory().length() < 2 || book.getCategory().length() > 30) errors.put("category", "Choose or type a category.");
        if (book.getYear() < 1000 || book.getYear() > thisYear + 1) errors.put("year", "Enter a year between 1000 and " + (thisYear + 1) + ".");
        if (book.getTotalCopies() < 1 || book.getTotalCopies() > 50) errors.put("totalCopies", "Copies must be between 1 and 50.");
        if (book.getDescription().length() > 400) errors.put("description", "Keep the description under 400 characters.");

        if (errors.isEmpty()) {
            library.addBook(book);
            Flash.success(request.getSession(), "Added \"" + book.getTitle() + "\" to the catalogue.");
            return "redirect:/admin.do";
        }
        request.setAttribute("book", book);
        request.setAttribute("errors", errors);
        return "bookForm";
    }
}
