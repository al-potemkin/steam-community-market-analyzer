package org.bmarket.steam.service.retry;


import lombok.extern.log4j.Log4j2;
import org.bmarket.steam.exception.RetryException;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Log4j2
public final class RetryLogic {
    private RetryLogic() {
    }

    private static final String RETRY_EXCEPTION_MESSAGE = "Retrying failed to complete successfully after %s attempts";

    public static <T> Supplier<T> retry(Supplier<T> supplier,
                                        int maxRetries,
                                        long sleepTime,
                                        TimeUnit timeUnit) {
        return () -> {
            var attemptNumber = 0;
            while (attemptNumber < maxRetries) {
                try {
                    return supplier.get();
                } catch (Exception e) {
                    attemptNumber++;
                    log.warn("-#- Attempt number {}. Message: {}", attemptNumber, e.getMessage());
                    sleepUntilNextTry(sleepTime, timeUnit, attemptNumber);
                }
            }
            throw new RetryException(String.format(RETRY_EXCEPTION_MESSAGE, maxRetries));
        };
    }

    private static void sleepUntilNextTry(long sleepTime, TimeUnit timeUnit, int attemptNumber) {
        try {
            Thread.sleep(timeUnit.toMillis(sleepTime));
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new RetryException(String.format(RETRY_EXCEPTION_MESSAGE, attemptNumber));
        }
    }
}
