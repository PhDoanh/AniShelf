package com.library.backend.exceptions;

public class DatabaseException extends RuntimeException {
    public DatabaseException(String message) {
        super(message);
    }

    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }

    public static class ConnectionException extends DatabaseException {
        public ConnectionException(String message, Throwable cause) {
            super("Database connection error: " + message, cause);
        }
    }

    public static class QueryException extends DatabaseException {
        public QueryException(String message, Throwable cause) {
            super("Database query error: " + message, cause);
        }
    }
}