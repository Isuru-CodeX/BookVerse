package com.bookverse.data;

import com.bookverse.bean.FeedbackBean;
import com.bookverse.model.*;

import java.sql.*;
import java.util.Date;

/**
 * JDBC implementation of the DAO for MySQL / MariaDB. Uses PreparedStatements only (no SQL injection)
 * and one short-lived connection per operation. A production system would add a connection pool.
 */
public class JdbcStore implements LibraryStore {
    private final String url;
    private final String user;
    private final String password;

    public JdbcStore(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    @Override public String name() { return "MySQL database"; }

    private Connection open() throws SQLException { return DriverManager.getConnection(url, user, password); }

    private static Timestamp ts(Date d) { return d == null ? null : new Timestamp(d.getTime()); }
    private static Date date(Timestamp t) { return t == null ? null : new Date(t.getTime()); }
    private static int sortOrder(String planType) {
        switch (planType) { case "STANDARD": return 1; case "PREMIUM": return 2; case "STUDENT": return 3; default: return 9; }
    }

    // ------------------------------------------------------------------ load
    @Override
    public Snapshot load() {
        Snapshot s = new Snapshot();
        try (Connection c = open(); Statement st = c.createStatement()) {
            try (ResultSet rs = st.executeQuery("SELECT type, label, description, max_loans, loan_days, short_loan_days, "
                    + "max_renewals, monthly_fee FROM membership_plans ORDER BY sort_order, type")) {
                while (rs.next()) {
                    s.plans.add(new MembershipPlan(rs.getString(1), rs.getString(2), rs.getString(3), rs.getInt(4),
                            rs.getInt(5), rs.getInt(6), rs.getInt(7), rs.getDouble(8)));
                }
            }
            try (ResultSet rs = st.executeQuery("SELECT id, name, email, password_hash, role, membership_type, join_date, active "
                    + "FROM members ORDER BY id")) {
                while (rs.next()) {
                    Member m = new Member();
                    m.setId(rs.getInt(1)); m.setName(rs.getString(2)); m.setEmail(rs.getString(3));
                    m.setPasswordHash(rs.getString(4)); m.setRole(rs.getString(5)); m.setMembershipType(rs.getString(6));
                    m.setJoinDate(date(rs.getTimestamp(7))); m.setActive(rs.getBoolean(8));
                    s.members.add(m);
                }
            }
            try (ResultSet rs = st.executeQuery("SELECT id, title, author, category, isbn, description, publish_year, "
                    + "total_copies, available_copies, rating FROM books ORDER BY id")) {
                while (rs.next()) {
                    Book b = new Book();
                    b.setId(rs.getInt(1)); b.setTitle(rs.getString(2)); b.setAuthor(rs.getString(3));
                    b.setCategory(rs.getString(4)); b.setIsbn(nz(rs.getString(5))); b.setDescription(nz(rs.getString(6)));
                    b.setYear(rs.getInt(7)); b.setTotalCopies(rs.getInt(8)); b.setAvailableCopies(rs.getInt(9));
                    b.setRating(rs.getDouble(10));
                    s.books.add(b);
                }
            }
            try (ResultSet rs = st.executeQuery("SELECT id, book_id, book_title, member_email, reserved_on, due_date, "
                    + "returned_on, status, renewals FROM reservations ORDER BY id")) {
                while (rs.next()) {
                    Reservation r = new Reservation();
                    r.setId(rs.getInt(1)); r.setBookId(rs.getInt(2)); r.setBookTitle(rs.getString(3));
                    r.setMemberEmail(rs.getString(4)); r.setReservedOn(date(rs.getTimestamp(5)));
                    r.setDueDate(date(rs.getTimestamp(6))); r.setReturnedOn(date(rs.getTimestamp(7)));
                    r.setStatus(rs.getString(8)); r.setRenewals(rs.getInt(9));
                    s.reservations.add(r);
                }
            }
            try (ResultSet rs = st.executeQuery("SELECT id, title, message, posted_on FROM announcements ORDER BY id")) {
                while (rs.next()) {
                    s.announcements.add(new Announcement(rs.getInt(1), rs.getString(2), rs.getString(3), date(rs.getTimestamp(4))));
                }
            }
            try (ResultSet rs = st.executeQuery("SELECT id, name, email, rating, comments, submitted_on FROM feedback ORDER BY id")) {
                while (rs.next()) {
                    FeedbackBean f = new FeedbackBean(date(rs.getTimestamp(6)));
                    f.setId(rs.getInt(1)); f.setName(rs.getString(2)); f.setEmail(rs.getString(3));
                    f.setRating(String.valueOf(rs.getInt(4))); f.setComments(rs.getString(5));
                    s.feedback.add(f);
                }
            }
            return s;
        } catch (SQLException e) {
            throw new DataAccessException("Could not load data from the database: " + e.getMessage(), e);
        }
    }

    private static String nz(String s) { return s == null ? "" : s; }

    // ------------------------------------------------------------------ books
    private static final String UPSERT_BOOK = "INSERT INTO books (id, title, author, category, isbn, description, publish_year, "
            + "total_copies, available_copies, rating) VALUES (?,?,?,?,?,?,?,?,?,?) ON DUPLICATE KEY UPDATE "
            + "title=VALUES(title), author=VALUES(author), category=VALUES(category), isbn=VALUES(isbn), "
            + "description=VALUES(description), publish_year=VALUES(publish_year), total_copies=VALUES(total_copies), "
            + "available_copies=VALUES(available_copies), rating=VALUES(rating)";

    private void bindBook(PreparedStatement ps, Book b) throws SQLException {
        ps.setInt(1, b.getId()); ps.setString(2, b.getTitle()); ps.setString(3, b.getAuthor());
        ps.setString(4, b.getCategory()); ps.setString(5, b.getIsbn()); ps.setString(6, b.getDescription());
        ps.setInt(7, b.getYear()); ps.setInt(8, b.getTotalCopies()); ps.setInt(9, b.getAvailableCopies());
        ps.setDouble(10, b.getRating());
    }

    @Override
    public void saveBook(Book b) {
        try (Connection c = open(); PreparedStatement ps = c.prepareStatement(UPSERT_BOOK)) {
            bindBook(ps, b);
            ps.executeUpdate();
        } catch (SQLException e) { throw fail("save book", e); }
    }

    @Override
    public void deleteBook(int id) { simpleDelete("DELETE FROM books WHERE id = ?", id); }

    // ------------------------------------------------------------------ members
    @Override
    public void saveMember(Member m) {
        String sql = "INSERT INTO members (id, name, email, password_hash, role, membership_type, join_date, active) "
                + "VALUES (?,?,?,?,?,?,?,?) ON DUPLICATE KEY UPDATE name=VALUES(name), email=VALUES(email), "
                + "password_hash=VALUES(password_hash), role=VALUES(role), membership_type=VALUES(membership_type), "
                + "active=VALUES(active)";
        try (Connection c = open(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, m.getId()); ps.setString(2, m.getName()); ps.setString(3, m.getEmail());
            ps.setString(4, m.getPasswordHash()); ps.setString(5, m.getRole()); ps.setString(6, m.getMembershipType());
            ps.setTimestamp(7, ts(m.getJoinDate())); ps.setBoolean(8, m.isActive());
            ps.executeUpdate();
        } catch (SQLException e) { throw fail("save member", e); }
    }

    // ------------------------------------------------------------------ loans (transaction)
    @Override
    public void saveLoan(Reservation r, Book book) {
        String sql = "INSERT INTO reservations (id, book_id, book_title, member_email, reserved_on, due_date, returned_on, "
                + "status, renewals) VALUES (?,?,?,?,?,?,?,?,?) ON DUPLICATE KEY UPDATE due_date=VALUES(due_date), "
                + "returned_on=VALUES(returned_on), status=VALUES(status), renewals=VALUES(renewals)";
        try (Connection c = open()) {
            c.setAutoCommit(false);
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setInt(1, r.getId()); ps.setInt(2, r.getBookId()); ps.setString(3, r.getBookTitle());
                ps.setString(4, r.getMemberEmail()); ps.setTimestamp(5, ts(r.getReservedOn()));
                ps.setTimestamp(6, ts(r.getDueDate())); ps.setTimestamp(7, ts(r.getReturnedOn()));
                ps.setString(8, r.getStatus()); ps.setInt(9, r.getRenewals());
                ps.executeUpdate();
                if (book != null) {
                    try (PreparedStatement ub = c.prepareStatement("UPDATE books SET available_copies = ? WHERE id = ?")) {
                        ub.setInt(1, book.getAvailableCopies());
                        ub.setInt(2, book.getId());
                        ub.executeUpdate();
                    }
                }
                c.commit();
            } catch (SQLException e) {
                c.rollback();
                throw e;
            }
        } catch (SQLException e) { throw fail("save loan", e); }
    }

    // ------------------------------------------------------------------ plans, announcements, feedback
    @Override
    public void savePlan(MembershipPlan p) {
        String sql = "INSERT INTO membership_plans (type, label, description, max_loans, loan_days, short_loan_days, "
                + "max_renewals, monthly_fee, sort_order) VALUES (?,?,?,?,?,?,?,?,?) ON DUPLICATE KEY UPDATE "
                + "label=VALUES(label), description=VALUES(description), max_loans=VALUES(max_loans), "
                + "loan_days=VALUES(loan_days), short_loan_days=VALUES(short_loan_days), max_renewals=VALUES(max_renewals), "
                + "monthly_fee=VALUES(monthly_fee)";
        try (Connection c = open(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, p.getType()); ps.setString(2, p.getLabel()); ps.setString(3, p.getDescription());
            ps.setInt(4, p.getMaxLoans()); ps.setInt(5, p.getLoanDays()); ps.setInt(6, p.getShortLoanDays());
            ps.setInt(7, p.getMaxRenewals()); ps.setDouble(8, p.getMonthlyFee()); ps.setInt(9, sortOrder(p.getType()));
            ps.executeUpdate();
        } catch (SQLException e) { throw fail("save plan", e); }
    }

    @Override
    public void saveAnnouncement(Announcement a) {
        String sql = "INSERT INTO announcements (id, title, message, posted_on) VALUES (?,?,?,?) "
                + "ON DUPLICATE KEY UPDATE title=VALUES(title), message=VALUES(message)";
        try (Connection c = open(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, a.getId()); ps.setString(2, a.getTitle()); ps.setString(3, a.getMessage());
            ps.setTimestamp(4, ts(a.getPostedOn()));
            ps.executeUpdate();
        } catch (SQLException e) { throw fail("save announcement", e); }
    }

    @Override
    public void deleteAnnouncement(int id) { simpleDelete("DELETE FROM announcements WHERE id = ?", id); }

    @Override
    public void saveFeedback(FeedbackBean f) {
        String sql = "INSERT INTO feedback (id, name, email, rating, comments, submitted_on) VALUES (?,?,?,?,?,?) "
                + "ON DUPLICATE KEY UPDATE name=VALUES(name), email=VALUES(email), rating=VALUES(rating), comments=VALUES(comments)";
        try (Connection c = open(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, f.getId()); ps.setString(2, f.getName()); ps.setString(3, f.getEmail());
            ps.setInt(4, f.getRatingValue()); ps.setString(5, f.getComments()); ps.setTimestamp(6, ts(f.getSubmittedOn()));
            ps.executeUpdate();
        } catch (SQLException e) { throw fail("save feedback", e); }
    }

    @Override
    public void deleteFeedback(int id) { simpleDelete("DELETE FROM feedback WHERE id = ?", id); }

    // ------------------------------------------------------------------ helpers
    private void simpleDelete(String sql, int id) {
        try (Connection c = open(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { throw fail("delete", e); }
    }

    private DataAccessException fail(String what, SQLException e) {
        return new DataAccessException("Database error while trying to " + what + ": " + e.getMessage(), e);
    }
}
