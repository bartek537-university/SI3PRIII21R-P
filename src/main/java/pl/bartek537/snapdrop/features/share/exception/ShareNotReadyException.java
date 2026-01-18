package pl.bartek537.snapdrop.features.share.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(HttpStatus.TOO_EARLY)
public class ShareNotReadyException extends RuntimeException {
    public ShareNotReadyException(UUID shareId) {
        super("Share is yet ready: " + shareId);
    }
}
