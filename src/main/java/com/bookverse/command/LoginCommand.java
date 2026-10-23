package com.bookverse.command;

import com.bookverse.bean.LoginForm;
import com.bookverse.model.Member;
import com.bookverse.service.LibraryDelegate;
import com.bookverse.service.LibraryException;
import com.bookverse.util.Flash;
import com.bookverse.util.Web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

/** GET shows the form; POST authenticates and stores the Member in session scope. */
public class LoginCommand implements Command {
    /** Only internal "/something.do?x=y" targets are accepted (prevents open redirects). */
    private static final Pattern SAFE_REDIRECT = Pattern.compile("^/[A-Za-z]+\\.do(\\?[\\w=&%.\\-]*)?$");
    private final LibraryDelegate library = new LibraryDelegate();

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) {
        if (!Web.isPost(request)) return "login";

        LoginForm form = new LoginForm();
        form.setEmail(Web.param(request, "email"));
        form.setPassword(request.getParameter("password"));
        form.setRedirect(Web.param(request, "redirect"));
        Map<String, String> errors = new LinkedHashMap<>();
        if (form.getEmail().isEmpty()) errors.put("email", "Enter your email address.");
        if (form.getPassword().isEmpty()) errors.put("password", "Enter your password.");

        if (errors.isEmpty()) {
            try {
                Member member = library.login(form.getEmail(), form.getPassword());
                request.getSession(true);
                request.changeSessionId();   // new session id after authentication (prevents session fixation)
                request.getSession().setAttribute("member", member);
                Flash.success(request.getSession(), "Welcome back, " + member.getName() + ".");
                String target = SAFE_REDIRECT.matcher(form.getRedirect()).matches() ? form.getRedirect() : "/dashboard.do";
                return "redirect:" + target;
            } catch (LibraryException e) {
                errors.put("login", e.getMessage());
            }
        }
        request.setAttribute("errors", errors);
        form.setPassword("");
        request.setAttribute("loginForm", form);
        return "login";
    }
}
