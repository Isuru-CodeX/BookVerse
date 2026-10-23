package com.bookverse.command;

import com.bookverse.model.*;
import com.bookverse.service.*;
import com.bookverse.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/** Staff: all loans, filterable by status (all / active / overdue / returned). */
public class AdminLoansCommand implements Command {
    private final LibraryDelegate library = new LibraryDelegate();

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) {
        String status = Web.param(request, "status");
        if (!Arrays.asList("active", "overdue", "returned").contains(status)) status = "all";
        List<Reservation> list = new ArrayList<>();
        for (Reservation r : library.allReservations()) {
            boolean keep = "all".equals(status)
                    || ("active".equals(status) && r.isActive())
                    || ("overdue".equals(status) && r.isOverdue())
                    || ("returned".equals(status) && !r.isActive());
            if (keep) list.add(r);
        }
        request.setAttribute("loans", list);
        request.setAttribute("status", status);
        return "adminLoans";
    }
}
