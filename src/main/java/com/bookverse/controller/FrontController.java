package com.bookverse.controller;

import com.bookverse.command.*;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Front Controller (MVC "C"): every *.do request enters here.
 * It selects a Command, executes it and dispatches to the JSP view (MVC "V")
 * using data placed in request/session scope by the command (MVC "M").
 */
public class FrontController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String VIEW_PREFIX = "/WEB-INF/views/";
    private static final String VIEW_SUFFIX = ".jsp";
    private final transient Map<String, Command> commands = new HashMap<>();

    @Override
    public void init() {
        commands.put("home", new HomeCommand());
        commands.put("catalog", new CatalogCommand());
        commands.put("bookDetail", new BookDetailCommand());
        commands.put("login", new LoginCommand());
        commands.put("logout", new LogoutCommand());
        commands.put("register", new RegisterCommand());
        commands.put("dashboard", new DashboardCommand());
        commands.put("reserve", new ReserveCommand());
        commands.put("return", new ReturnBookCommand());
        commands.put("renew", new RenewCommand());
        commands.put("changePlan", new ChangePlanCommand());
        commands.put("plans", new PlansCommand());
        // staff area: every command whose name starts with "admin" is protected by AuthFilter
        commands.put("admin", new AdminCommand());
        commands.put("adminBooks", new AdminBooksCommand());
        commands.put("adminBook", new AdminBookCommand());
        commands.put("adminBookDelete", new AdminBookDeleteCommand());
        commands.put("adminMembers", new AdminMembersCommand());
        commands.put("adminMember", new AdminMemberCommand());
        commands.put("adminLoans", new AdminLoansCommand());
        commands.put("adminLoanAction", new AdminLoanActionCommand());
        commands.put("adminPlans", new AdminPlansCommand());
        commands.put("adminAnnouncements", new AdminAnnouncementsCommand());
        commands.put("adminFeedback", new AdminFeedbackCommand());
        commands.put("adminReports", new AdminReportsCommand());
        commands.put("feedbackSubmit", new FeedbackSubmitCommand());
        commands.put("elDemo", new ElDemoCommand());
        commands.put("about", (req, res) -> "about");   // trivial command as a lambda
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();                                   // e.g. /catalog.do
        String name = path.substring(1, path.length() - ".do".length());      // e.g. catalog
        Command command = commands.get(name);
        if (command == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        try {
            String result = command.execute(req, resp);
            if (result == null) return;
            if (result.startsWith("redirect:")) {
                resp.sendRedirect(resp.encodeRedirectURL(req.getContextPath() + result.substring("redirect:".length())));
                return;
            }
            RequestDispatcher view = req.getRequestDispatcher(VIEW_PREFIX + result + VIEW_SUFFIX);
            view.forward(req, resp);
        } catch (ServletException | IOException e) {
            throw e;
        } catch (Exception e) {
            // Unexpected failure: hand over to the <error-page> mapping (error.jsp)
            throw new ServletException("Command '" + name + "' failed: " + e.getMessage(), e);
        }
    }
}
