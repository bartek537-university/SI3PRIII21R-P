package pl.bartek537.snapdrop.features.share.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ShareNotFoundException extends RuntimeException {
    public ShareNotFoundException(UUID shareId) {
        super("Share not found: " + shareId);
    }

    public ShareNotFoundException(String slug) {
        super("Share not found: " + slug);
    }
}
