package com.bookverse.data;

/** Unchecked wrapper for persistence failures (SQLException etc.). */
public class DataAccessException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    public DataAccessException(String message, Throwable cause) { super(message, cause); }
}
