package com.bookverse.command;

import com.bookverse.model.Member;
import com.bookverse.model.Reservation;
import com.bookverse.service.LibraryDelegate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/** Member dashboard: active loans and borrowing history (login enforced by AuthFilter). */
public class DashboardCommand implements Command {
    private final LibraryDelegate library = new LibraryDelegate();

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) {
        Member member = (Member) request.getSession().getAttribute("member");
        List<Reservation> all = library.reservationsFor(member.getEmail());
        long active = all.stream().filter(Reservation::isActive).count();
        request.setAttribute("reservations", all);
        request.setAttribute("activeCount", active);
        com.bookverse.model.MembershipPlan plan = library.plan(member.getMembershipType());
        request.setAttribute("plan", plan);
        request.setAttribute("maxLoans", plan.getMaxLoans());
        request.setAttribute("plans", library.plans());
        return "dashboard";
    }
}
