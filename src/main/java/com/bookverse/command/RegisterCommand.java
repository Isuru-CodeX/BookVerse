package com.bookverse.command;

import com.bookverse.bean.RegistrationForm;
import com.bookverse.model.Member;
import com.bookverse.service.LibraryDelegate;
import com.bookverse.service.LibraryException;
import com.bookverse.util.Flash;
import com.bookverse.util.Web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/** Member registration with server-side validation. */
public class RegisterCommand implements Command {
    private final LibraryDelegate library = new LibraryDelegate();

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) {
        request.setAttribute("plans", library.plans());
        if (!Web.isPost(request)) return "register";

        RegistrationForm form = new RegistrationForm();
        form.setName(Web.param(request, "name"));
        form.setEmail(Web.param(request, "email"));
        form.setPassword(request.getParameter("password"));
        form.setConfirmPassword(request.getParameter("confirmPassword"));
        form.setMembershipType(Web.param(request, "membershipType"));

        Map<String, String> errors = form.validate();
        if (errors.isEmpty()) {
            try {
                Member member = library.register(form);
                request.getSession(true);
                request.changeSessionId();   // new session id after authentication (prevents session fixation)
                request.getSession().setAttribute("member", member);
                Flash.success(request.getSession(), "Your account is ready, " + member.getName() + ". Start by browsing the catalogue.");
                return "redirect:/dashboard.do";
            } catch (LibraryException e) {
                errors.put("email", e.getMessage());
            }
        }
        form.setPassword("");            // never echo passwords back
        form.setConfirmPassword("");
        request.setAttribute("form", form);
        request.setAttribute("errors", errors);
        return "register";
    }
}
