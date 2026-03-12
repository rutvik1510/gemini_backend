package org.hartford.eventguard.exception;

public class DuplicateSubscriptionException extends RuntimeException {

    public DuplicateSubscriptionException(String message) {
        super(message);
    }
}
