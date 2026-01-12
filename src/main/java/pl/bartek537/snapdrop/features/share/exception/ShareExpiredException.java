package pl.bartek537.snapdrop.features.share.exception;

import java.util.UUID;

public class ShareExpiredException extends RuntimeException {
    public ShareExpiredException(UUID shareId) {
        super("Share expired: " + shareId);
    }
}
