package com.bookverse.command;

import com.bookverse.model.*;
import com.bookverse.service.*;
import com.bookverse.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/** Staff: edit the booking rules of each membership plan. */
public class AdminPlansCommand implements Command {
    private final LibraryDelegate library = new LibraryDelegate();

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) {
        if (Web.isPost(request)) {
            String type = Web.param(request, "type");
            try {
                MembershipPlan p = library.updatePlan(type, Web.param(request, "description"),
                        Web.intParam(request, "maxLoans", -1), Web.intParam(request, "loanDays", -1),
                        Web.intParam(request, "shortLoanDays", -1), Web.intParam(request, "maxRenewals", -1),
                        Web.doubleParam(request, "monthlyFee", -1));
                Flash.success(request.getSession(), "The " + p.getLabel() + " plan was updated. New rules apply to future reservations and renewals.");
            } catch (LibraryException e) {
                Flash.error(request.getSession(), e.getMessage());
            }
            return "redirect:/adminPlans.do";
        }
        Map<String, Integer> counts = new HashMap<>();
        for (Member m : library.allMembers()) counts.merge(m.getMembershipType(), 1, Integer::sum);
        request.setAttribute("plans", library.plans());
        request.setAttribute("planCounts", counts);
        return "adminPlans";
    }
}
