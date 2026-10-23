package com.bookverse.model;

import java.io.Serializable;
import java.util.Date;

/** Transfer Object for a library announcement. */
public class Announcement implements Serializable {
    private static final long serialVersionUID = 1L;
    private int id;
    private String title;
    private String message;
    private Date postedOn = new Date();

    public Announcement() { }
    public Announcement(int id, String title, String message, Date postedOn) {
        this.id = id; this.title = title; this.message = message; this.postedOn = postedOn;
    }
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public Date getPostedOn() { return postedOn; }
    public void setPostedOn(Date postedOn) { this.postedOn = postedOn; }
}
