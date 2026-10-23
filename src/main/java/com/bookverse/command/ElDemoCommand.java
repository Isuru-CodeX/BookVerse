package com.bookverse.command;

import com.bookverse.service.LibraryDelegate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Prepares sample data for the Expression Language lab page. */
public class ElDemoCommand implements Command {
    private final LibraryDelegate library = new LibraryDelegate();

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) {
        Map<String, String> sampleMap = new LinkedHashMap<>();
        sampleMap.put("one", "First");
        sampleMap.put("two", "Second");
        sampleMap.put("key.with.dots", "Needs the [] operator");

        request.setAttribute("demoMessage", "Set by ElDemoCommand in request scope");
        request.setAttribute("books", library.allBooks());
        request.setAttribute("sampleMap", sampleMap);
        request.setAttribute("sampleArray", new String[] {"JSP", "EL", "JSTL", "Tags"});
        request.setAttribute("sampleList", List.of(10, 20, 30));
        request.getSession().setAttribute("demoNote", "Set in session scope");
        return "elDemo";
    }
}
