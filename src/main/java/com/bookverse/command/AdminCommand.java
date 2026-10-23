package com.bookverse.command;

import com.bookverse.model.*;
import com.bookverse.service.*;
import com.bookverse.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/** Staff overview: statistics plus items that need attention (overdue loans, sold-out titles). */
public class AdminCommand implements Command {
    private final LibraryDelegate library = new LibraryDelegate();

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) {
        List<Reservation> overdue = new ArrayList<>();
        for (Reservation r : library.allReservations()) if (r.isOverdue()) overdue.add(r);
        List<Book> soldOut = new ArrayList<>();
        for (Book b : library.allBooks()) if (!b.isAvailable()) soldOut.add(b);
        request.setAttribute("stats", library.stats());
        request.setAttribute("overdue", overdue);
        request.setAttribute("soldOut", soldOut);
        request.setAttribute("announcements", library.announcements(3));
        return "admin";
    }
}
