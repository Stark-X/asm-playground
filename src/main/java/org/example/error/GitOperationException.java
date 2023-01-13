package org.example.error;

public class GitOperationException extends Exception {
    public GitOperationException(String message) {
        super(message);
    }

    public GitOperationException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public GitOperationException(Throwable throwable) {
        super(throwable);
    }
}
