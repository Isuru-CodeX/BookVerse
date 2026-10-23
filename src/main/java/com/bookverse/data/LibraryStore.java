package com.bookverse.data;

import com.bookverse.bean.FeedbackBean;
import com.bookverse.model.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) contract. The business service keeps its working data in memory and
 * writes every change through this interface, so the same service runs on plain memory
 * ({@link MemoryStore}) or on a MySQL database ({@link JdbcStore}).
 */
public interface LibraryStore {

    /** Everything stored, used once at start-up. */
    class Snapshot {
        public final List<MembershipPlan> plans = new ArrayList<>();
        public final List<Member> members = new ArrayList<>();
        public final List<Book> books = new ArrayList<>();
        public final List<Reservation> reservations = new ArrayList<>();
        public final List<Announcement> announcements = new ArrayList<>();
        public final List<FeedbackBean> feedback = new ArrayList<>();

        public boolean isEmpty() { return members.isEmpty() && books.isEmpty(); }
    }

    /** Human readable name shown in the page footer. */
    String name();

    Snapshot load();

    void saveBook(Book book);
    void deleteBook(int id);
    void saveMember(Member member);
    /** Saves a loan and, in the same transaction, the book whose stock changed (may be null). */
    void saveLoan(Reservation loan, Book book);
    void savePlan(MembershipPlan plan);
    void saveAnnouncement(Announcement announcement);
    void deleteAnnouncement(int id);
    void saveFeedback(FeedbackBean feedback);
    void deleteFeedback(int id);
}
