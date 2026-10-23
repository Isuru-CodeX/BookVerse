package com.bookverse.command;

import com.bookverse.model.*;
import com.bookverse.service.*;
import com.bookverse.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/** Staff: add a title (no id) or edit an existing one (?id=). GET shows the form, POST saves. */
public class AdminBookCommand implements Command {
    private final LibraryDelegate library = new LibraryDelegate();

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request.setAttribute("categories", library.categories());
        int thisYear = Calendar.getInstance().get(Calendar.YEAR);

        if (!Web.isPost(request)) {
            int id = Web.intParam(request, "id", 0);
            Book book;
            if (id > 0) {
                book = library.findBook(id);
                if (book == null) { response.sendError(HttpServletResponse.SC_NOT_FOUND); return null; }
            } else {
                book = new Book();
                book.setYear(thisYear);
                book.setTotalCopies(1);
            }
            request.setAttribute("book", book);
            return "bookForm";
        }

        Map<String, String> errors = new LinkedHashMap<>();
        Book book = new Book();
        book.setId(Web.intParam(request, "id", 0));
        book.setTitle(Web.param(request, "title"));
        book.setAuthor(Web.param(request, "author"));
        book.setCategory(Web.param(request, "category"));
        book.setIsbn(Web.param(request, "isbn"));
        book.setDescription(Web.param(request, "description"));
        book.setYear(Web.intParam(request, "year", 0));
        book.setTotalCopies(Web.intParam(request, "totalCopies", 0));
        book.setRating(Web.doubleParam(request, "rating", -1));

        if (book.getTitle().length() < 2 || book.getTitle().length() > 120) errors.put("title", "Enter a title (2-120 characters).");
        if (book.getAuthor().length() < 2 || book.getAuthor().length() > 120) errors.put("author", "Enter the author or publisher.");
        if (book.getCategory().length() < 2 || book.getCategory().length() > 30) errors.put("category", "Choose or type a category.");
        if (book.getYear() < 1000 || book.getYear() > thisYear + 1) errors.put("year", "Enter a year between 1000 and " + (thisYear + 1) + ".");
        if (book.getTotalCopies() < 1 || book.getTotalCopies() > 50) errors.put("totalCopies", "Copies must be between 1 and 50.");
        if (book.getRating() < 0 || book.getRating() > 5) errors.put("rating", "Rating must be between 0 and 5.");
        if (book.getDescription().length() > 400) errors.put("description", "Keep the description under 400 characters.");

        if (errors.isEmpty()) {
            try {
                if (book.getId() > 0) {
                    library.updateBook(book);
                    Flash.success(request.getSession(), "Saved changes to \"" + book.getTitle() + "\".");
                } else {
                    library.addBook(book);
                    Flash.success(request.getSession(), "Added \"" + book.getTitle() + "\" to the catalogue.");
                }
                return "redirect:/adminBooks.do";
            } catch (LibraryException e) {
                errors.put("totalCopies", e.getMessage());
            }
        }
        request.setAttribute("book", book);
        request.setAttribute("errors", errors);
        return "bookForm";
    }
}
