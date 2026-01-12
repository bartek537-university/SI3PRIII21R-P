package pl.bartek537.snapdrop.features.share.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class AttachmentNotFoundException extends RuntimeException {
    public AttachmentNotFoundException(UUID attachmentId, UUID shareId) {
        super("Attachment not found: " + attachmentId + " for share: " + shareId);
    }
}
