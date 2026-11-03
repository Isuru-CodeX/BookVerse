package com.bookverse.command;

import com.bookverse.model.*;
import com.bookverse.service.*;
import com.bookverse.util.*;

import javax.servlet.http.HttpServletRequest;
import com.bookverse.util.TextUtil;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/** Staff: all accounts with plan, role, status and number of open loans. */
public class AdminMembersCommand implements Command {
    private final LibraryDelegate library = new LibraryDelegate();

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) {
        String plan = Web.param(request, "plan");
        String q = Web.param(request, "q");
        List<Member> list = new ArrayList<>();
        for (Member m : library.allMembers()) {
            if (!plan.isEmpty() && !plan.equals(m.getMembershipType())) continue;
            // ⚡ Bolt: Prevented O(N) String allocations during search filtering by using TextUtil.containsIgnoreCase
            if (!q.isEmpty() && !TextUtil.containsIgnoreCase(m.getName(), q) && !TextUtil.containsIgnoreCase(m.getEmail(), q)) continue;
            list.add(m);
        }
        request.setAttribute("members", list);
        request.setAttribute("loanCounts", library.activeLoanCounts());
        request.setAttribute("plans", library.plans());
        request.setAttribute("planFilter", plan);
        request.setAttribute("q", Web.param(request, "q"));
        return "adminMembers";
    }
}
