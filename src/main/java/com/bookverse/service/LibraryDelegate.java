package com.bookverse.service;

import com.bookverse.bean.FeedbackBean;
import com.bookverse.bean.RegistrationForm;
import com.bookverse.bean.SearchCriteria;
import com.bookverse.model.Announcement;
import com.bookverse.model.Book;
import com.bookverse.model.Member;
import com.bookverse.model.MembershipPlan;
import com.bookverse.model.Reservation;

import java.util.List;
import java.util.Map;

/**
 * Business Delegate pattern: the only class the presentation tier (commands) talks to.
 * It hides how the service is located and shields commands from changes in the business tier.
 */
public class LibraryDelegate {
    private final LibraryService service =
            ServiceLocator.lookup(ServiceLocator.LIBRARY_SERVICE, LibraryService.class);

    public List<Book> search(SearchCriteria c)              { return service.search(c); }
    public Book findBook(int id)                            { return service.findBook(id); }
    public List<Book> topRated(int n)                       { return service.topRated(n); }
    public List<Book> newestAdditions(int n)                { return service.newestAdditions(n); }
    public List<Book> relatedTo(Book b, int n)              { return service.relatedTo(b, n); }
    public List<Book> allBooks()                            { return service.findAllBooks(); }
    public List<String> categories()                        { return service.getCategories(); }
    public Book addBook(Book b)                             { return service.addBook(b); }
    public Member login(String email, String pw) throws LibraryException { return service.authenticate(email, pw); }
    public Member register(RegistrationForm f) throws LibraryException    { return service.register(f); }
    public Reservation reserve(String email, int bookId) throws LibraryException { return service.reserve(email, bookId); }
    public Reservation returnBook(String email, int id) throws LibraryException   { return service.returnBook(email, id); }
    public List<Reservation> reservationsFor(String email)  { return service.reservationsFor(email); }
    public List<Reservation> allReservations()              { return service.allReservations(); }
    public Announcement postAnnouncement(String t, String m){ return service.postAnnouncement(t, m); }
    public List<Announcement> announcements(int n)          { return service.latestAnnouncements(n); }
    public void saveFeedback(FeedbackBean fb)               { service.addFeedback(fb); }
    public Map<String, Object> stats()                      { return service.getStats(); }

    // plans
    public List<MembershipPlan> plans()                     { return service.getPlans(); }
    public MembershipPlan plan(String type)                 { return service.getPlan(type); }
    public int loanDaysFor(Member m, Book b)                { return service.loanDaysFor(m, b); }
    public MembershipPlan updatePlan(String type, String desc, int maxLoans, int loanDays, int shortDays, int renewals, double fee)
            throws LibraryException { return service.updatePlan(type, desc, maxLoans, loanDays, shortDays, renewals, fee); }
    // members
    public Member findMemberById(int id)                    { return service.findMemberById(id); }
    public List<Member> allMembers()                        { return service.allMembers(); }
    public Map<String, Integer> activeLoanCounts()          { return service.activeLoanCounts(); }
    public Member updateMember(int id, String name, String type, String role, boolean active, String acting)
            throws LibraryException { return service.updateMember(id, name, type, role, active, acting); }
    public void resetPassword(int id, String pw) throws LibraryException { service.resetPassword(id, pw); }
    public Member changePlan(String email, String type) throws LibraryException { return service.changePlan(email, type); }
    // loans
    public Reservation renew(String email, int id) throws LibraryException { return service.renew(email, id); }
    public Reservation adminReturn(int id) throws LibraryException         { return service.adminReturn(id); }
    public Reservation extendLoan(int id, int days) throws LibraryException { return service.extendLoan(id, days); }
    // catalogue admin
    public Book updateBook(Book b) throws LibraryException { return service.updateBook(b); }
    public Book deleteBook(int id) throws LibraryException { return service.deleteBook(id); }
    // announcements, feedback, reports
    public List<Announcement> allAnnouncements()            { return service.allAnnouncements(); }
    public boolean deleteAnnouncement(int id)               { return service.deleteAnnouncement(id); }
    public List<FeedbackBean> allFeedback()                 { return service.allFeedback(); }
    public boolean deleteFeedback(int id)                   { return service.deleteFeedback(id); }
    public Map<String, Object> report()                     { return service.getReport(); }
}
