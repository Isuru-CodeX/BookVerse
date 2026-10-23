package com.bookverse.service;

/** Checked business-rule exception; its message is safe to show to the user. */
public class LibraryException extends Exception {
    private static final long serialVersionUID = 1L;
    public LibraryException(String message) { super(message); }
}
