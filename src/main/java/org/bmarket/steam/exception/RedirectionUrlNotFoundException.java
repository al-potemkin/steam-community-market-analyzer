package org.bmarket.steam.exception;

public class RedirectionUrlNotFoundException extends RuntimeException {
    public RedirectionUrlNotFoundException(String message) {
        super(message);
    }
}
