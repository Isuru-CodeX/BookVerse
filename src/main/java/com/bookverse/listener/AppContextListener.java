package com.bookverse.listener;

import com.bookverse.data.JdbcStore;
import com.bookverse.data.LibraryStore;
import com.bookverse.data.MemoryStore;
import com.bookverse.service.LibraryService;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Collections;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Initialises application-scope attributes and connects the business service to its data store.
 * Database settings come from environment variables (BOOKVERSE_DB_URL, _USER, _PASSWORD, _DRIVER) or,
 * when those are absent, from the db.* context-params in web.xml. An empty db.url means in-memory demo mode.
 */
public class AppContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent event) {
        ServletContext ctx = event.getServletContext();
        ctx.setAttribute("appName", ctx.getInitParameter("libraryName"));
        ctx.setAttribute("startedAt", new Date());
        ctx.setAttribute("visitCounter", new AtomicInteger());
        ctx.setAttribute("activeSessions", new AtomicInteger());
        try {
            LibraryService.configureDefaultStandardLoans(Integer.parseInt(ctx.getInitParameter("maxActiveLoans")));
        } catch (NumberFormatException e) {
            ctx.log("maxActiveLoans context-param missing or invalid; using default");
        }

        LibraryService service = LibraryService.getInstance();   // never null; starts in memory mode
        String url = setting(ctx, "BOOKVERSE_DB_URL", "db.url");
        String mode;
        if (url.isEmpty()) {
            service.init(new MemoryStore());
            mode = service.getStoreName() + " (no database configured)";
        } else {
            try {
                String driver = setting(ctx, "BOOKVERSE_DB_DRIVER", "db.driver");
                if (!driver.isEmpty()) Class.forName(driver, true, ctx.getClassLoader());
                LibraryStore store = new JdbcStore(url, setting(ctx, "BOOKVERSE_DB_USER", "db.user"),
                        setting(ctx, "BOOKVERSE_DB_PASSWORD", "db.password"));
                service.init(store);
                mode = store.name();
                ctx.log("[BookVerse] Connected to the database.");
            } catch (Exception e) {
                ctx.log("[BookVerse] SEVERE: database unavailable, falling back to in-memory data. Reason: " + e.getMessage(), e);
                service.init(new MemoryStore());
                mode = "In-memory demo data (database unavailable - see server log)";
            }
        }
        ctx.setAttribute("storageMode", mode);
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        // release JDBC drivers loaded by this web application (avoids class-loader leaks on redeploy)
        ClassLoader cl = event.getServletContext().getClassLoader();
        for (Driver d : Collections.list(DriverManager.getDrivers())) {
            if (d.getClass().getClassLoader() == cl) {
                try { DriverManager.deregisterDriver(d); } catch (Exception ignored) { /* shutting down */ }
            }
        }
    }

    private static String setting(ServletContext ctx, String env, String param) {
        String v = System.getenv(env);
        if (v == null || v.trim().isEmpty()) v = ctx.getInitParameter(param);
        return v == null ? "" : v.trim();
    }
}
