package pl.bartek537.snapdrop.features.share.exception;

import java.util.UUID;

public class EmptyShareClosureException extends RuntimeException {
    public EmptyShareClosureException(UUID shareId) {
        super("Cannot close a share with no attachments: " + shareId);
    }
}
