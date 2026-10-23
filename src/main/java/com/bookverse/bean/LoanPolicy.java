package com.bookverse.bean;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

/** Page-scope JavaBean that turns a loan length into a due date. */
public class LoanPolicy implements Serializable {
    private static final long serialVersionUID = 1L;
    private int loanDays = 14;

    public LoanPolicy() { }

    public int getLoanDays() { return loanDays; }
    public void setLoanDays(int loanDays) { this.loanDays = Math.max(1, loanDays); }

    /** Read-only property: today + loanDays. */
    public Date getDueDate() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_YEAR, loanDays);
        return c.getTime();
    }
}
