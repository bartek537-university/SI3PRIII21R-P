package pl.bartek537.snapdrop.features.share;

import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.bartek537.snapdrop.features.share.model.Attachment;

import java.util.UUID;

@RestController
@RequestMapping("shares/{shareId}/attachments")
public class AttachmentController {

    private final AttachmentService attachmentService;

    AttachmentController(AttachmentService attachmentService) {
        this.attachmentService = attachmentService;
    }

    @PostMapping
    public ResponseEntity<@NonNull Attachment> storeAttachment(@PathVariable UUID shareId, @RequestParam("file") MultipartFile file) {
        Attachment attachment = attachmentService.storeAttachment(shareId, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(attachment);
    }
}
