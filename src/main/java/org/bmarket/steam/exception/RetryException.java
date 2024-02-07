package org.bmarket.steam.exception;

public class RetryException extends RuntimeException {
    public RetryException(String message) {
        super(message);
    }
}
