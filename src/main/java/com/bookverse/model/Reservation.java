package com.bookverse.model;

import java.io.Serializable;
import java.util.Date;

/** Transfer Object for a reservation / borrowing record. */
public class Reservation implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final String ACTIVE = "ACTIVE";
    public static final String RETURNED = "RETURNED";

    private int id;
    private int bookId;
    private String bookTitle;
    private String memberEmail;
    private Date reservedOn;
    private Date dueDate;
    private Date returnedOn;
    private String status = ACTIVE;
    private int renewals;

    public Reservation() { }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getBookId() { return bookId; }
    public void setBookId(int bookId) { this.bookId = bookId; }
    public String getBookTitle() { return bookTitle; }
    public void setBookTitle(String bookTitle) { this.bookTitle = bookTitle; }
    public String getMemberEmail() { return memberEmail; }
    public void setMemberEmail(String memberEmail) { this.memberEmail = memberEmail; }
    public Date getReservedOn() { return reservedOn; }
    public void setReservedOn(Date reservedOn) { this.reservedOn = reservedOn; }
    public Date getDueDate() { return dueDate; }
    public void setDueDate(Date dueDate) { this.dueDate = dueDate; }
    public Date getReturnedOn() { return returnedOn; }
    public void setReturnedOn(Date returnedOn) { this.returnedOn = returnedOn; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getRenewals() { return renewals; }
    public void setRenewals(int renewals) { this.renewals = renewals; }

    public boolean isActive() { return ACTIVE.equals(status); }
    public boolean isOverdue() { return isActive() && dueDate != null && dueDate.before(new Date()); }
}
