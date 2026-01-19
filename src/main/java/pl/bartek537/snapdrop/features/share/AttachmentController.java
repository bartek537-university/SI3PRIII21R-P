package pl.bartek537.snapdrop.features.share;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.bartek537.snapdrop.features.share.dto.AttachmentDownload;
import pl.bartek537.snapdrop.features.share.model.Attachment;

import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.UUID;

import static pl.bartek537.snapdrop.Constants.MANAGEMENT_TOKEN_HEADER;

@RestController
@RequestMapping("api/shares/{shareId}/attachments")
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
        return addContentDownloadHeaders(ResponseEntity.ok(), download.fileName()).body(download.resource());
    }

    private ResponseEntity.BodyBuilder addContentDownloadHeaders(ResponseEntity.BodyBuilder builder, String fileName) {
        ContentDisposition contentDisposition = ContentDisposition.attachment()
                .filename(fileName, StandardCharsets.UTF_8).build();

        return builder.contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString());
    }

    @DeleteMapping("{attachmentId}")
    public void deleteAttachmentById(@PathVariable UUID shareId, @PathVariable UUID attachmentId,
                                     @RequestHeader(MANAGEMENT_TOKEN_HEADER) String token) {
        attachmentService.deleteAttachmentById(attachmentId, shareId, token);
    }
}
