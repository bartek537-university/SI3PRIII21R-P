package pl.bartek537.snapdrop.features.share;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.bartek537.snapdrop.features.share.dto.AttachmentDownload;
import pl.bartek537.snapdrop.features.share.model.Attachment;

import java.util.Set;
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
    public Attachment storeAttachment(@PathVariable UUID shareId, @RequestParam("file") MultipartFile file,
                                      @RequestHeader(MANAGEMENT_TOKEN_HEADER) String token) {
        return attachmentService.storeAttachment(shareId, token, file);
    }

    @GetMapping
    public Set<Attachment> getAttachmentsByShareId(@PathVariable UUID shareId) {
        return attachmentService.getAttachmentsByShareId(shareId);
    }

    @GetMapping("{attachmentId}")
    public Attachment getAttachmentById(@PathVariable UUID shareId, @PathVariable UUID attachmentId) {
        return attachmentService.getAttachmentById(attachmentId, shareId);
    }

    @GetMapping("{attachmentId}/file")
    public ResponseEntity<?> downloadAttachment(@PathVariable UUID shareId, @PathVariable UUID attachmentId,
                                                @RequestHeader(value = MANAGEMENT_TOKEN_HEADER, required = false) String token) {

        AttachmentDownload download = attachmentService.prepareAttachmentDownload(attachmentId, shareId, token);
        return addContentDisposition(ResponseEntity.ok(), download.fileName()).body(download.resource());
    }

    private ResponseEntity.BodyBuilder addContentDisposition(ResponseEntity.BodyBuilder builder, String fileName) {
        String contentDisposition = String.format("attachment; filename=\"%s\"", fileName);
        return builder.header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition);
    }

    @DeleteMapping("{attachmentId}")
    public void deleteAttachmentById(@PathVariable UUID shareId, @PathVariable UUID attachmentId,
                                     @RequestHeader(MANAGEMENT_TOKEN_HEADER) String token) {
        attachmentService.deleteAttachmentById(attachmentId, shareId, token);
    }
}
