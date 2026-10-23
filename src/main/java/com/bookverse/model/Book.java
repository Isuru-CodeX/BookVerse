package com.bookverse.model;

import java.io.Serializable;

/** Transfer Object representing a catalogue item (book, journal or magazine). */
public class Book implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String title = "";
    private String author = "";
    private String category = "";
    private String isbn = "";
    private String description = "";
    private int year;
    private int totalCopies;
    private int availableCopies;
    private double rating;

    public Book() { }

    public Book(int id, String title, String author, String category, int year,
                String isbn, int copies, double rating, String description) {
        this.id = id; this.title = title; this.author = author; this.category = category;
        this.year = year; this.isbn = isbn; this.totalCopies = copies;
        this.availableCopies = copies; this.rating = rating; this.description = description;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }
    public int getTotalCopies() { return totalCopies; }
    public void setTotalCopies(int totalCopies) { this.totalCopies = totalCopies; }
    public int getAvailableCopies() { return availableCopies; }
    public void setAvailableCopies(int availableCopies) { this.availableCopies = availableCopies; }
    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    /** Read-only (derived) property: ${book.available} */
    public boolean isAvailable() { return availableCopies > 0; }

    /** Journals and magazines are short-loan items. Read-only property: ${book.shortLoan} */
    public boolean isShortLoan() {
        return "Journal".equalsIgnoreCase(category) || "Magazine".equalsIgnoreCase(category);
    }

    /** Up to two capital letters used on the generated cover. Read-only property. */
    public String getInitials() {
        StringBuilder sb = new StringBuilder();
        for (String w : title.trim().split("\\s+")) {
            if (!w.isEmpty() && Character.isLetter(w.charAt(0)) && sb.length() < 2) {
                sb.append(Character.toUpperCase(w.charAt(0)));
            }
        }
        return sb.length() == 0 ? "BV" : sb.toString();
    }
}
