package com.bookverse.command;

import com.bookverse.model.*;
import com.bookverse.service.*;
import com.bookverse.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/** Staff: edit one account (name, plan, role, status) or reset its password. */
public class AdminMemberCommand implements Command {
    private final LibraryDelegate library = new LibraryDelegate();

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
        int id = Web.intParam(request, "id", -1);
        Member target = library.findMemberById(id);
        if (target == null) { response.sendError(HttpServletResponse.SC_NOT_FOUND, "Member not found."); return null; }
        Member me = (Member) request.getSession().getAttribute("member");

        if (Web.isPost(request)) {
            String action = Web.param(request, "action");
            try {
                if ("password".equals(action)) {
                    library.resetPassword(id, request.getParameter("newPassword") == null ? "" : request.getParameter("newPassword"));
                    Flash.success(request.getSession(), "Password reset for " + target.getName() + ".");
                } else {
                    library.updateMember(id, Web.param(request, "name"), Web.param(request, "membershipType"),
                            Web.param(request, "role"), "true".equals(Web.param(request, "active")), me.getEmail());
                    Flash.success(request.getSession(), "Saved changes for " + target.getName() + ".");
                }
            } catch (LibraryException e) {
                Flash.error(request.getSession(), e.getMessage());
            }
            return "redirect:/adminMember.do?id=" + id;
        }
        request.setAttribute("target", target);
        request.setAttribute("plans", library.plans());
        request.setAttribute("loans", library.reservationsFor(target.getEmail()));
        request.setAttribute("isSelf", target.getId() == me.getId());
        return "memberEdit";
    }
}
