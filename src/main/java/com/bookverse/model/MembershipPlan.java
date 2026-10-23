package com.bookverse.model;

import java.io.Serializable;

/** Transfer Object: the booking rules attached to a membership type (STANDARD, PREMIUM, STUDENT). */
public class MembershipPlan implements Serializable {
    private static final long serialVersionUID = 1L;
    private String type;
    private String label;
    private String description;
    private int maxLoans;
    private int loanDays;
    private int shortLoanDays;
    private int maxRenewals;
    private double monthlyFee;

    public MembershipPlan() { }

    public MembershipPlan(String type, String label, String description, int maxLoans,
                          int loanDays, int shortLoanDays, int maxRenewals, double monthlyFee) {
        this.type = type; this.label = label; this.description = description; this.maxLoans = maxLoans;
        this.loanDays = loanDays; this.shortLoanDays = shortLoanDays; this.maxRenewals = maxRenewals;
        this.monthlyFee = monthlyFee;
    }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public int getMaxLoans() { return maxLoans; }
    public void setMaxLoans(int maxLoans) { this.maxLoans = maxLoans; }
    public int getLoanDays() { return loanDays; }
    public void setLoanDays(int loanDays) { this.loanDays = loanDays; }
    public int getShortLoanDays() { return shortLoanDays; }
    public void setShortLoanDays(int shortLoanDays) { this.shortLoanDays = shortLoanDays; }
    public int getMaxRenewals() { return maxRenewals; }
    public void setMaxRenewals(int maxRenewals) { this.maxRenewals = maxRenewals; }
    public double getMonthlyFee() { return monthlyFee; }
    public void setMonthlyFee(double monthlyFee) { this.monthlyFee = monthlyFee; }
}
