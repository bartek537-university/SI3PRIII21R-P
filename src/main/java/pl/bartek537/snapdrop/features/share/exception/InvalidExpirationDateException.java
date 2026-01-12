package pl.bartek537.snapdrop.features.share.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.Instant;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidExpirationDateException extends RuntimeException {
    public InvalidExpirationDateException(Instant instant) {
        super("Invalid expiration date: " + instant);
    }
}
