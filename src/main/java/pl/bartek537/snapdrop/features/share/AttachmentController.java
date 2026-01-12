package pl.bartek537.snapdrop.features.share;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.bartek537.snapdrop.features.share.dto.AttachmentDownload;
import pl.bartek537.snapdrop.features.share.model.Attachment;

import java.util.UUID;

import static pl.bartek537.snapdrop.Constants.MANAGEMENT_TOKEN_HEADER;

@RestController
@RequestMapping("shares/{shareId}/attachments")
public class AttachmentController {

    private final AttachmentService attachmentService;

    AttachmentController(AttachmentService attachmentService) {
        this.attachmentService = attachmentService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Attachment storeAttachment(@PathVariable UUID shareId, @RequestHeader(MANAGEMENT_TOKEN_HEADER) String token, @RequestParam("file") MultipartFile file) {
        return attachmentService.storeAttachment(shareId, token, file);
    }

    @GetMapping("{attachmentId}")
    public Attachment getAttachmentById(@PathVariable UUID shareId, @PathVariable UUID attachmentId) {
        return attachmentService.getAttachmentById(attachmentId, shareId);
    }

    @GetMapping("{attachmentId}/file")
    public ResponseEntity<?> downloadAttachment(@PathVariable UUID shareId, @PathVariable UUID attachmentId) {
        AttachmentDownload download = attachmentService.prepareAttachmentDownload(attachmentId, shareId);

        String contentDisposition = String.format("attachment; filename=\"%s\"", download.fileName());
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                .body(download.resource());
    }
}
