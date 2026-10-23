package com.bookverse.bean;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * JavaBean populated directly by {@code <jsp:setProperty name="fb" property="*"/>}.
 * "rating" is a String so that bad input becomes a validation message instead of an exception.
 */
public class FeedbackBean implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Pattern EMAIL = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private int id;
    private String name = "";
    private String email = "";
    private String rating = "";
    private String comments = "";
    private final Date submittedOn;

    public FeedbackBean() { this.submittedOn = new Date(); }

    /** Used when rows are restored from the database. */
    public FeedbackBean(Date submittedOn) { this.submittedOn = submittedOn == null ? new Date() : submittedOn; }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }   // overwritten by the service on save
    public String getName() { return name; }
    public void setName(String name) { this.name = name == null ? "" : name.trim(); }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email == null ? "" : email.trim(); }
    public String getRating() { return rating; }
    public void setRating(String rating) { this.rating = rating == null ? "" : rating.trim(); }
    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments == null ? "" : comments.trim(); }
    public Date getSubmittedOn() { return submittedOn; }

    /** Numeric rating, or 0 when missing/invalid. Read-only property ${fb.ratingValue}. */
    public int getRatingValue() {
        try { return Integer.parseInt(rating); } catch (NumberFormatException e) { return 0; }
    }

    /** Read-only property ${fb.errors}; map of field to message. */
    public Map<String, String> getErrors() {
        Map<String, String> errors = new LinkedHashMap<>();
        if (name.length() < 2 || name.length() > 60) errors.put("name", "Enter your name (2-60 characters).");
        if (!EMAIL.matcher(email).matches()) errors.put("email", "Enter a valid email address.");
        int r = getRatingValue();
        if (r < 1 || r > 5) errors.put("rating", "Choose a rating from 1 to 5.");
        if (comments.length() < 10 || comments.length() > 500) {
            errors.put("comments", "Tell us a little more (10-500 characters).");
        }
        return errors;
    }

    public boolean isValid() { return getErrors().isEmpty(); }
}
