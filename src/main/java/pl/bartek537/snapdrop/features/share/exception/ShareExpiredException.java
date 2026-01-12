package pl.bartek537.snapdrop.features.share.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(HttpStatus.CONFLICT)
public class ShareExpiredException extends RuntimeException {
    public ShareExpiredException(UUID shareId) {
        super("Share expired: " + shareId);
    }
}
