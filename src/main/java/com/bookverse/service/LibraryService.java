package com.bookverse.service;

import com.bookverse.bean.FeedbackBean;
import com.bookverse.data.LibraryStore;
import com.bookverse.data.MemoryStore;
import com.bookverse.bean.RegistrationForm;
import com.bookverse.bean.SearchCriteria;
import com.bookverse.model.Announcement;
import com.bookverse.model.Book;
import com.bookverse.model.Member;
import com.bookverse.model.MembershipPlan;
import com.bookverse.model.Reservation;
import com.bookverse.util.PasswordUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.regex.Pattern;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * In-memory business service (no database needed for this assessment).
 * State-changing methods are synchronized so concurrent requests stay consistent.
 */
public class LibraryService {
    private static final LibraryService INSTANCE = new LibraryService();
    private static volatile int defaultStandardLoans = 3;

    /** Returns the shared service; falls back to in-memory data if nobody called {@link #init} yet. */
    public static LibraryService getInstance() {
        synchronized (INSTANCE) {
            if (!INSTANCE.initialized) INSTANCE.init(new MemoryStore());
        }
        return INSTANCE;
    }

    /** Default loan limit of the STANDARD plan, used only when the plans are first created. */
    public static void configureDefaultStandardLoans(int n) { defaultStandardLoans = Math.max(1, n); }

    private final List<Book> books = new CopyOnWriteArrayList<>();
    private final List<Member> members = new CopyOnWriteArrayList<>();
    private final List<Reservation> reservations = new CopyOnWriteArrayList<>();
    private final List<Announcement> announcements = new CopyOnWriteArrayList<>();
    private final List<FeedbackBean> feedback = new CopyOnWriteArrayList<>();
    private final AtomicInteger bookSeq = new AtomicInteger();
    private final AtomicInteger memberSeq = new AtomicInteger();
    private final AtomicInteger reservationSeq = new AtomicInteger();
    private final AtomicInteger announcementSeq = new AtomicInteger();
    private final AtomicInteger feedbackSeq = new AtomicInteger();
    private final Map<String, MembershipPlan> plans = new LinkedHashMap<>();

    private LibraryStore store = new MemoryStore();
    private boolean initialized;
    private boolean seeding;

    private LibraryService() { }

    /**
     * Connects the service to a store and loads its data. An empty store is filled with the sample
     * data; a store that already has data is loaded as it is.
     */
    public synchronized void init(LibraryStore newStore) {
        this.store = newStore;
        books.clear(); members.clear(); reservations.clear(); announcements.clear(); feedback.clear(); plans.clear();
        bookSeq.set(0); memberSeq.set(0); reservationSeq.set(0); announcementSeq.set(0); feedbackSeq.set(0);

        LibraryStore.Snapshot snap = newStore.load();
        if (snap.isEmpty()) {
            seeding = true;
            try { seed(); } finally { seeding = false; }
            persistAll();
        } else {
            for (var p : snap.plans) plans.put(p.getType(), p);
            if (plans.isEmpty()) { defaultPlans(); for (var p : plans.values()) store.savePlan(p); }
            members.addAll(snap.members);
            books.addAll(snap.books);
            reservations.addAll(snap.reservations);
            announcements.addAll(snap.announcements);
            feedback.addAll(snap.feedback);
            members.forEach(m -> memberSeq.accumulateAndGet(m.getId(), Math::max));
            books.forEach(b -> bookSeq.accumulateAndGet(b.getId(), Math::max));
            reservations.forEach(r -> reservationSeq.accumulateAndGet(r.getId(), Math::max));
            announcements.forEach(a -> announcementSeq.accumulateAndGet(a.getId(), Math::max));
            feedback.forEach(f -> feedbackSeq.accumulateAndGet(f.getId(), Math::max));
        }
        initialized = true;
    }

    public String getStoreName() { return store.name(); }

    private void persistAll() {
        plans.values().forEach(store::savePlan);
        members.forEach(store::saveMember);
        books.forEach(store::saveBook);
        reservations.forEach(r -> store.saveLoan(r, null));
        announcements.forEach(store::saveAnnouncement);
    }

    // persistence helpers (skipped while the sample data is being created, persistAll() saves it afterwards)
    private void saveBook(Book b)                  { if (!seeding) store.saveBook(b); }
    private void saveMember(Member m)              { if (!seeding) store.saveMember(m); }
    private void saveLoan(Reservation r, Book b)   { if (!seeding) store.saveLoan(r, b); }
    private void savePlan(MembershipPlan p)        { if (!seeding) store.savePlan(p); }
    private void saveAnnouncement(Announcement a)  { if (!seeding) store.saveAnnouncement(a); }

    // ------------------------------------------------------------------ seed data
    private void defaultPlans() {
        plans.put("STANDARD", new MembershipPlan("STANDARD", "Standard", "Everyday borrowing for general readers.", defaultStandardLoans, 14, 7, 1, 500));
        plans.put("PREMIUM", new MembershipPlan("PREMIUM", "Premium", "More titles, the longest loans and the most renewals.", 8, 28, 14, 3, 1500));
        plans.put("STUDENT", new MembershipPlan("STUDENT", "Student", "Free for enrolled students, with extended study loans.", 5, 21, 10, 2, 0));
    }

    private void seed() {
        defaultPlans();
        addBook(new Book(0, "Clean Code", "Robert C. Martin", "Technology", 2008, "9780132350884", 3, 4.6,
                "A handbook of agile software craftsmanship: naming, functions, formatting and refactoring habits that keep code readable."));
        addBook(new Book(0, "Effective Java", "Joshua Bloch", "Technology", 2018, "9780134685991", 2, 4.8,
                "Programming-language guide with 90 items on writing clear, correct and robust Java."));
        addBook(new Book(0, "Head First Servlets and JSP", "Bryan Basham, Kathy Sierra, Bert Bates", "Technology", 2008, "9780596516680", 2, 4.4,
                "A visual, hands-on guide to servlets, JSP, EL, JSTL and custom tags for the Java EE web tier."));
        addBook(new Book(0, "The Pragmatic Programmer", "Andrew Hunt, David Thomas", "Technology", 2019, "9780135957059", 2, 4.5,
                "Practical advice on tools, habits and attitudes for working developers."));
        addBook(new Book(0, "A Brief History of Time", "Stephen Hawking", "Science", 1988, "9780553380163", 2, 4.3,
                "An accessible tour of black holes, the Big Bang and the nature of time."));
        addBook(new Book(0, "Cosmos", "Carl Sagan", "Science", 1980, "9780345539434", 1, 4.6,
                "A personal voyage through the universe and the history of scientific thought."));
        addBook(new Book(0, "Sapiens", "Yuval Noah Harari", "History", 2011, "9780062316097", 1, 4.4,
                "A brief history of humankind, from the Stone Age to the present."));
        addBook(new Book(0, "Guns, Germs, and Steel", "Jared Diamond", "History", 1997, "9780393317558", 2, 4.1,
                "Why human societies developed so differently across the continents."));
        addBook(new Book(0, "1984", "George Orwell", "Fiction", 1949, "9780451524935", 3, 4.7,
                "A dystopian classic about surveillance, truth and power."));
        addBook(new Book(0, "Pride and Prejudice", "Jane Austen", "Fiction", 1813, "9780141439518", 2, 4.5,
                "Manners, marriage and misjudgement in Regency England."));
        addBook(new Book(0, "The Alchemist", "Paulo Coelho", "Fiction", 1988, "9780062315007", 2, 4.0,
                "A shepherd's journey in search of a treasure and his personal legend."));
        addBook(new Book(0, "Web Engineering Review", "BookVerse Editorial Board", "Journal", 2025, "ISSN 0000-0001", 2, 3.9,
                "Quarterly journal covering web architecture, Java EE and front-end engineering (sample issue)."));
        addBook(new Book(0, "Science Today", "BookVerse Editorial Board", "Magazine", 2026, "ISSN 0000-0002", 4, 4.2,
                "Monthly magazine of popular science news and long reads (sample issue)."));
        addBook(new Book(0, "Educator's Digest", "BookVerse Editorial Board", "Magazine", 2026, "ISSN 0000-0003", 3, 3.8,
                "Teaching ideas, study techniques and campus stories (sample issue)."));

        Member staff = newMember("Library Admin", "admin@bookverse.lk", "admin123", Member.ROLE_STAFF, "PREMIUM");
        Member member = newMember("Nimali Perera", "member@bookverse.lk", "member123", Member.ROLE_MEMBER, "STANDARD");
        members.add(staff);
        members.add(member);

        try {
            reserve(member.getEmail(), 1);   // Clean Code
            reserve(member.getEmail(), 7);   // Sapiens (only copy -> unavailable for others)
        } catch (LibraryException e) {
            throw new IllegalStateException(e);
        }
        Reservation past = new Reservation();
        past.setId(reservationSeq.incrementAndGet());
        past.setBookId(5);
        past.setBookTitle("A Brief History of Time");
        past.setMemberEmail(member.getEmail());
        past.setReservedOn(daysFromNow(-40));
        past.setDueDate(daysFromNow(-26));
        past.setReturnedOn(daysFromNow(-30));
        past.setStatus(Reservation.RETURNED);
        reservations.add(past);

        postAnnouncement("Extended weekend hours", "The reading room is now open until 8 p.m. on Saturdays and Sundays.");
        postAnnouncement("New journal issue", "The latest issue of Web Engineering Review is on the shelves and ready to reserve.");
        postAnnouncement("Membership drive", "Students can join for free this month. Register online and start reserving today.");
    }

    private Member newMember(String name, String email, String password, String role, String type) {
        Member m = new Member();
        m.setId(memberSeq.incrementAndGet());
        m.setName(name);
        m.setEmail(email);
        m.setPasswordHash(PasswordUtil.hash(email, password));
        m.setRole(role);
        m.setMembershipType(type);
        m.setJoinDate(new Date());
        return m;
    }

    private static Date daysFromNow(int days) {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_YEAR, days);
        return c.getTime();
    }

    // ------------------------------------------------------------------ configuration
    /* Plan limits are data: edit them on the staff "Plans" tab. */

    // ------------------------------------------------------------------ catalogue
    public List<Book> findAllBooks() { return new ArrayList<>(books); }

    public Book findBook(int id) {
        for (Book b : books) if (b.getId() == id) return b;
        return null;
    }

    public List<Book> search(SearchCriteria c) {
        String kw = c.getKeyword().toLowerCase();
        Comparator<Book> order;
        switch (c.getSort()) {
            case "rating": order = Comparator.comparingDouble(Book::getRating).reversed(); break;
            case "year":   order = Comparator.comparingInt(Book::getYear).reversed(); break;
            // ⚡ Bolt: Prevented O(N log N) String allocations during sorting by using CASE_INSENSITIVE_ORDER
            default:       order = Comparator.comparing(Book::getTitle, String.CASE_INSENSITIVE_ORDER);
        }
        return books.stream()
                .filter(b -> kw.isEmpty() || b.getTitle().toLowerCase().contains(kw) || b.getAuthor().toLowerCase().contains(kw))
                .filter(b -> c.getCategory().isEmpty() || b.getCategory().equalsIgnoreCase(c.getCategory()))
                .filter(b -> !c.isAvailableOnly() || b.isAvailable())
                .sorted(order)
                .collect(Collectors.toList());
    }

    public List<Book> topRated(int n) {
        return books.stream().sorted(Comparator.comparingDouble(Book::getRating).reversed()).limit(n).collect(Collectors.toList());
    }

    public List<Book> newestAdditions(int n) {
        List<Book> copy = new ArrayList<>(books);
        Collections.reverse(copy);
        return copy.subList(0, Math.min(n, copy.size()));
    }

    public List<Book> relatedTo(Book book, int n) {
        return books.stream().filter(b -> b.getId() != book.getId() && b.getCategory().equalsIgnoreCase(book.getCategory()))
                .limit(n).collect(Collectors.toList());
    }

    public List<String> getCategories() {
        TreeSet<String> set = new TreeSet<>();
        for (Book b : books) set.add(b.getCategory());
        return new ArrayList<>(set);
    }

    public synchronized Book addBook(Book b) {
        b.setId(bookSeq.incrementAndGet());
        b.setAvailableCopies(b.getTotalCopies());
        books.add(b);
        saveBook(b);
        return b;
    }

    // ------------------------------------------------------------------ members
    public Member authenticate(String email, String password) throws LibraryException {
        String hash = PasswordUtil.hash(email, password);
        for (Member m : members) {
            if (m.getEmail().equalsIgnoreCase(email) && m.getPasswordHash().equals(hash)) {
                if (!m.isActive()) throw new LibraryException("This account is suspended. Please contact library staff.");
                return m;
            }
        }
        throw new LibraryException("Email or password is incorrect.");
    }

    public synchronized Member register(RegistrationForm f) throws LibraryException {
        for (Member m : members) {
            if (m.getEmail().equalsIgnoreCase(f.getEmail())) {
                throw new LibraryException("An account with this email already exists. Try signing in instead.");
            }
        }
        Member m = newMember(f.getName(), f.getEmail(), f.getPassword(), Member.ROLE_MEMBER, f.getMembershipType());
        members.add(m);
        saveMember(m);
        return m;
    }

    // ------------------------------------------------------------------ reservations
    public synchronized Reservation reserve(String email, int bookId) throws LibraryException {
        Member member = findMember(email);
        if (member == null || !member.isActive()) throw new LibraryException("Your account cannot reserve titles right now.");
        Book book = findBook(bookId);
        if (book == null) throw new LibraryException("That title does not exist in the catalogue.");
        if (!book.isAvailable()) throw new LibraryException("No copies of \"" + book.getTitle() + "\" are available right now.");
        MembershipPlan plan = getPlan(member.getMembershipType());
        int active = 0;
        for (Reservation r : reservations) {
            if (r.isActive() && r.getMemberEmail().equalsIgnoreCase(email)) {
                active++;
                if (r.getBookId() == bookId) throw new LibraryException("You already have \"" + book.getTitle() + "\" on loan.");
            }
        }
        if (active >= plan.getMaxLoans()) {
            throw new LibraryException("Your " + plan.getLabel() + " plan allows " + plan.getMaxLoans()
                    + " active loans. Return a title first or upgrade your plan.");
        }
        Reservation r = new Reservation();
        r.setId(reservationSeq.incrementAndGet());
        r.setBookId(book.getId());
        r.setBookTitle(book.getTitle());
        r.setMemberEmail(email);
        r.setReservedOn(new Date());
        r.setDueDate(daysFromNow(loanDaysFor(member, book)));
        reservations.add(r);
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        saveLoan(r, book);
        return r;
    }

    public synchronized Reservation returnBook(String email, int reservationId) throws LibraryException {
        for (Reservation r : reservations) {
            if (r.getId() == reservationId) {
                if (!r.getMemberEmail().equalsIgnoreCase(email)) throw new LibraryException("That loan belongs to another member.");
                if (!r.isActive()) throw new LibraryException("This title has already been returned.");
                completeReturn(r);
                return r;
            }
        }
        throw new LibraryException("Loan record not found.");
    }

    public List<Reservation> reservationsFor(String email) {
        List<Reservation> out = new ArrayList<>();
        for (Reservation r : reservations) if (r.getMemberEmail().equalsIgnoreCase(email)) out.add(r);
        out.sort(Comparator.comparing(Reservation::getReservedOn).reversed());
        return out;
    }

    public List<Reservation> allReservations() {
        List<Reservation> out = new ArrayList<>(reservations);
        out.sort(Comparator.comparing(Reservation::getReservedOn).reversed());
        return out;
    }

    // ------------------------------------------------------------------ announcements & feedback
    public synchronized Announcement postAnnouncement(String title, String message) {
        Announcement a = new Announcement(announcementSeq.incrementAndGet(), title, message, new Date());
        announcements.add(a);
        saveAnnouncement(a);
        return a;
    }

    public List<Announcement> latestAnnouncements(int n) {
        List<Announcement> copy = new ArrayList<>(announcements);
        Collections.reverse(copy);
        return copy.subList(0, Math.min(n, copy.size()));
    }

    public synchronized void addFeedback(FeedbackBean fb) {
        fb.setId(feedbackSeq.incrementAndGet());
        feedback.add(fb);
        store.saveFeedback(fb);
    }


    // ------------------------------------------------------------------ membership plans
    public synchronized List<MembershipPlan> getPlans() { return new ArrayList<>(plans.values()); }

    public synchronized MembershipPlan getPlan(String type) {
        MembershipPlan p = plans.get(type);
        return p != null ? p : plans.get("STANDARD");
    }

    /** Booking rule: long loans for books, short loans for journals/magazines, both from the member's plan. */
    public int loanDaysFor(Member member, Book book) {
        MembershipPlan plan = getPlan(member == null ? "STANDARD" : member.getMembershipType());
        return book.isShortLoan() ? plan.getShortLoanDays() : plan.getLoanDays();
    }

    public synchronized MembershipPlan updatePlan(String type, String description, int maxLoans, int loanDays,
                                                  int shortLoanDays, int maxRenewals, double fee) throws LibraryException {
        MembershipPlan p = plans.get(type);
        if (p == null) throw new LibraryException("Unknown membership plan.");
        if (maxLoans < 1 || maxLoans > 20) throw new LibraryException("Loan limit must be between 1 and 20.");
        if (loanDays < 1 || loanDays > 90) throw new LibraryException("Book loan period must be between 1 and 90 days.");
        if (shortLoanDays < 1 || shortLoanDays > 30) throw new LibraryException("Journal/magazine loan period must be between 1 and 30 days.");
        if (maxRenewals < 0 || maxRenewals > 5) throw new LibraryException("Renewals must be between 0 and 5.");
        if (fee < 0 || fee > 100000) throw new LibraryException("Monthly fee must be between 0 and 100,000.");
        if (description.length() > 120) throw new LibraryException("Keep the description under 120 characters.");
        p.setDescription(description);
        p.setMaxLoans(maxLoans);
        p.setLoanDays(loanDays);
        p.setShortLoanDays(shortLoanDays);
        p.setMaxRenewals(maxRenewals);
        p.setMonthlyFee(fee);
        savePlan(p);
        return p;
    }

    // ------------------------------------------------------------------ member administration
    private static final Pattern PASSWORD_OK = Pattern.compile("^.{6,100}$");

    public Member findMember(String email) {
        for (Member m : members) if (m.getEmail().equalsIgnoreCase(email)) return m;
        return null;
    }

    public Member findMemberById(int id) {
        for (Member m : members) if (m.getId() == id) return m;
        return null;
    }

    public List<Member> allMembers() { return new ArrayList<>(members); }

    public synchronized Member updateMember(int id, String name, String type, String role, boolean active,
                                            String actingEmail) throws LibraryException {
        Member m = findMemberById(id);
        if (m == null) throw new LibraryException("Member not found.");
        if (!com.bookverse.bean.RegistrationForm.isValidName(name)) throw new LibraryException("Enter a valid full name.");
        if (!plans.containsKey(type)) throw new LibraryException("Choose a valid membership plan.");
        if (!Member.ROLE_MEMBER.equals(role) && !Member.ROLE_STAFF.equals(role)) throw new LibraryException("Choose a valid role.");
        if (m.getEmail().equalsIgnoreCase(actingEmail) && (!active || !Member.ROLE_STAFF.equals(role))) {
            throw new LibraryException("You cannot suspend or demote your own account.");
        }
        int open = activeLoanCount(m.getEmail());
        if (open > plans.get(type).getMaxLoans()) {
            throw new LibraryException(m.getName() + " has " + open + " active loans, more than the " + plans.get(type).getLabel()
                    + " plan allows (" + plans.get(type).getMaxLoans() + "). Ask them to return titles first.");
        }
        m.setName(name);
        m.setMembershipType(type);
        m.setRole(role);
        m.setActive(active);
        saveMember(m);
        return m;
    }

    public synchronized void resetPassword(int id, String newPassword) throws LibraryException {
        Member m = findMemberById(id);
        if (m == null) throw new LibraryException("Member not found.");
        if (!PASSWORD_OK.matcher(newPassword).matches()) throw new LibraryException("Use a password with at least 6 characters.");
        m.setPasswordHash(PasswordUtil.hash(m.getEmail(), newPassword));
        saveMember(m);
    }

    /** Self-service switch between the paid plans. STUDENT is granted by staff only. */
    public synchronized Member changePlan(String email, String type) throws LibraryException {
        Member m = findMember(email);
        if (m == null) throw new LibraryException("Member not found.");
        if (!"STANDARD".equals(type) && !"PREMIUM".equals(type)) throw new LibraryException("You can switch between Standard and Premium. Ask staff about the Student plan.");
        MembershipPlan target = plans.get(type);
        int open = activeLoanCount(email);
        if (open > target.getMaxLoans()) {
            throw new LibraryException("You hold " + open + " loans but the " + target.getLabel() + " plan allows " + target.getMaxLoans() + ". Return a title first.");
        }
        m.setMembershipType(type);
        saveMember(m);
        return m;
    }

    public int activeLoanCount(String email) {
        int n = 0;
        for (Reservation r : reservations) if (r.isActive() && r.getMemberEmail().equalsIgnoreCase(email)) n++;
        return n;
    }

    public Map<String, Integer> activeLoanCounts() {
        Map<String, Integer> out = new LinkedHashMap<>();
        for (Reservation r : reservations) if (r.isActive()) out.merge(r.getMemberEmail().toLowerCase(), 1, Integer::sum);
        return out;
    }

    // ------------------------------------------------------------------ loan administration
    public Reservation findReservation(int id) {
        for (Reservation r : reservations) if (r.getId() == id) return r;
        return null;
    }

    private void completeReturn(Reservation r) {
        r.setStatus(Reservation.RETURNED);
        r.setReturnedOn(new Date());
        Book b = findBook(r.getBookId());
        if (b != null) b.setAvailableCopies(Math.min(b.getTotalCopies(), b.getAvailableCopies() + 1));
        saveLoan(r, b);
    }

    private static Date addDays(Date from, int days) {
        Calendar c = Calendar.getInstance();
        c.setTime(from);
        c.add(Calendar.DAY_OF_YEAR, days);
        return c.getTime();
    }

    /** Member renewal: limited by the plan's renewal allowance; extends by one plan loan period. */
    public synchronized Reservation renew(String email, int reservationId) throws LibraryException {
        Reservation r = findReservation(reservationId);
        if (r == null) throw new LibraryException("Loan record not found.");
        if (!r.getMemberEmail().equalsIgnoreCase(email)) throw new LibraryException("That loan belongs to another member.");
        if (!r.isActive()) throw new LibraryException("This title has already been returned.");
        Member m = findMember(email);
        MembershipPlan plan = getPlan(m == null ? "STANDARD" : m.getMembershipType());
        if (r.isOverdue()) throw new LibraryException("Overdue loans cannot be renewed. Please return the title.");
        if (r.getRenewals() >= plan.getMaxRenewals()) {
            throw new LibraryException("Your " + plan.getLabel() + " plan allows " + plan.getMaxRenewals()
                    + (plan.getMaxRenewals() == 1 ? " renewal" : " renewals") + " per loan.");
        }
        Book b = findBook(r.getBookId());
        int days = b == null ? plan.getLoanDays() : loanDaysFor(m, b);
        r.setDueDate(addDays(r.getDueDate(), days));
        r.setRenewals(r.getRenewals() + 1);
        saveLoan(r, null);
        return r;
    }

    public synchronized Reservation adminReturn(int reservationId) throws LibraryException {
        Reservation r = findReservation(reservationId);
        if (r == null) throw new LibraryException("Loan record not found.");
        if (!r.isActive()) throw new LibraryException("This loan is already closed.");
        completeReturn(r);
        return r;
    }

    public synchronized Reservation extendLoan(int reservationId, int days) throws LibraryException {
        Reservation r = findReservation(reservationId);
        if (r == null) throw new LibraryException("Loan record not found.");
        if (!r.isActive()) throw new LibraryException("Only active loans can be extended.");
        if (days < 1 || days > 30) throw new LibraryException("Extension must be between 1 and 30 days.");
        r.setDueDate(addDays(r.getDueDate().before(new Date()) ? new Date() : r.getDueDate(), days));
        saveLoan(r, null);
        return r;
    }

    // ------------------------------------------------------------------ catalogue administration
    public synchronized Book updateBook(Book f) throws LibraryException {
        Book b = findBook(f.getId());
        if (b == null) throw new LibraryException("Title not found.");
        int onLoan = b.getTotalCopies() - b.getAvailableCopies();
        if (f.getTotalCopies() < onLoan) {
            throw new LibraryException("Copies cannot drop below the " + onLoan + " currently on loan.");
        }
        b.setTitle(f.getTitle());
        b.setAuthor(f.getAuthor());
        b.setCategory(f.getCategory());
        b.setIsbn(f.getIsbn());
        b.setYear(f.getYear());
        b.setDescription(f.getDescription());
        b.setRating(f.getRating());
        b.setTotalCopies(f.getTotalCopies());
        b.setAvailableCopies(f.getTotalCopies() - onLoan);
        saveBook(b);
        return b;
    }

    public synchronized Book deleteBook(int id) throws LibraryException {
        Book b = findBook(id);
        if (b == null) throw new LibraryException("Title not found.");
        for (Reservation r : reservations) {
            if (r.isActive() && r.getBookId() == id) throw new LibraryException("\"" + b.getTitle() + "\" is on loan and cannot be removed yet.");
        }
        books.remove(b);
        store.deleteBook(id);
        return b;
    }

    // ------------------------------------------------------------------ announcements, feedback, reports
    public List<Announcement> allAnnouncements() {
        List<Announcement> copy = new ArrayList<>(announcements);
        Collections.reverse(copy);
        return copy;
    }

    public synchronized boolean deleteAnnouncement(int id) {
        boolean removed = announcements.removeIf(a -> a.getId() == id);
        if (removed) store.deleteAnnouncement(id);
        return removed;
    }

    public List<FeedbackBean> allFeedback() {
        List<FeedbackBean> copy = new ArrayList<>(feedback);
        Collections.reverse(copy);
        return copy;
    }

    public synchronized boolean deleteFeedback(int id) {
        boolean removed = feedback.removeIf(f -> f.getId() == id);
        if (removed) store.deleteFeedback(id);
        return removed;
    }

    public Map<String, Object> getReport() {
        Map<String, Integer> perTitle = new LinkedHashMap<>();
        reservations.stream().collect(Collectors.groupingBy(Reservation::getBookTitle, Collectors.summingInt(r -> 1)))
                .entrySet().stream().sorted(Map.Entry.<String, Integer>comparingByValue().reversed()).limit(6)
                .forEach(e -> perTitle.put(e.getKey(), e.getValue()));

        // ⚡ Bolt: Optimized O(N*M) nested loops to O(N+M) using frequency maps for plan members and category copies
        Map<String, Integer> memberCounts = new java.util.HashMap<>();
        for (Member m : members) memberCounts.merge(m.getMembershipType(), 1, Integer::sum);
        Map<String, Integer> perPlan = new LinkedHashMap<>();
        for (MembershipPlan p : plans.values()) {
            perPlan.put(p.getLabel(), memberCounts.getOrDefault(p.getType(), 0));
        }

        Map<String, Integer> categoryCopies = new java.util.HashMap<>();
        for (Book b : books) categoryCopies.merge(b.getCategory(), b.getTotalCopies(), Integer::sum);
        Map<String, Integer> perCategory = new LinkedHashMap<>();
        for (String c : getCategories()) {
            perCategory.put(c, categoryCopies.getOrDefault(c, 0));
        }
        int active = 0, overdue = 0, returned = 0;
        for (Reservation r : reservations) {
            if (r.isOverdue()) overdue++; else if (r.isActive()) active++; else returned++;
        }
        Map<String, Integer> byStatus = new LinkedHashMap<>();
        byStatus.put("On loan", active);
        byStatus.put("Overdue", overdue);
        byStatus.put("Returned", returned);

        double sum = 0;
        for (FeedbackBean f : feedback) sum += f.getRatingValue();

        Map<String, Object> out = new LinkedHashMap<>();
        out.put("loansPerTitle", perTitle);
        out.put("membersByPlan", perPlan);
        out.put("copiesByCategory", perCategory);
        out.put("loansByStatus", byStatus);
        out.put("maxTitleLoans", perTitle.values().stream().mapToInt(Integer::intValue).max().orElse(1));
        out.put("maxPlanMembers", perPlan.values().stream().mapToInt(Integer::intValue).max().orElse(1));
        out.put("maxCategoryCopies", perCategory.values().stream().mapToInt(Integer::intValue).max().orElse(1));
        out.put("maxStatus", Math.max(1, byStatus.values().stream().mapToInt(Integer::intValue).max().orElse(1)));
        out.put("averageFeedback", feedback.isEmpty() ? 0.0 : sum / feedback.size());
        return out;
    }

    // ------------------------------------------------------------------ statistics
    public Map<String, Object> getStats() {
        Map<String, Object> s = new LinkedHashMap<>();
        int copies = 0, available = 0, active = 0, overdue = 0;
        for (Book b : books) { copies += b.getTotalCopies(); available += b.getAvailableCopies(); }
        for (Reservation r : reservations) { if (r.isActive()) active++; if (r.isOverdue()) overdue++; }
        s.put("titles", books.size());
        s.put("copies", copies);
        s.put("availableCopies", available);
        s.put("activeLoans", active);
        s.put("overdueLoans", overdue);
        s.put("members", members.size());
        s.put("feedbackCount", feedback.size());
        return s;
    }
}
