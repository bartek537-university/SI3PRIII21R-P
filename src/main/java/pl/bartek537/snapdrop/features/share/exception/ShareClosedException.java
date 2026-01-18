package pl.bartek537.snapdrop.features.share.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(HttpStatus.CONFLICT)
public class ShareClosedException extends RuntimeException {
    public ShareClosedException(UUID shareId) {
        super("Share is already closed: " + shareId);
    }
}
