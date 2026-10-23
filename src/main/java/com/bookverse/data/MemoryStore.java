package com.bookverse.data;

import com.bookverse.bean.FeedbackBean;
import com.bookverse.model.*;

/** No-op store: data lives only in the service's memory and resets on restart (demo mode). */
public class MemoryStore implements LibraryStore {
    private final String label;

    public MemoryStore() { this("In-memory demo data"); }
    public MemoryStore(String label) { this.label = label; }

    @Override public String name() { return label; }
    @Override public Snapshot load() { return new Snapshot(); }
    @Override public void saveBook(Book book) { }
    @Override public void deleteBook(int id) { }
    @Override public void saveMember(Member member) { }
    @Override public void saveLoan(Reservation loan, Book book) { }
    @Override public void savePlan(MembershipPlan plan) { }
    @Override public void saveAnnouncement(Announcement announcement) { }
    @Override public void deleteAnnouncement(int id) { }
    @Override public void saveFeedback(FeedbackBean feedback) { }
    @Override public void deleteFeedback(int id) { }
}
