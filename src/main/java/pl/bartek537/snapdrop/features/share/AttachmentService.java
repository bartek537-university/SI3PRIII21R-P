package pl.bartek537.snapdrop.features.share;

import jakarta.transaction.Transactional;
import org.springframework.core.io.Resource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pl.bartek537.snapdrop.features.share.dto.AttachmentDownload;
import pl.bartek537.snapdrop.features.share.exception.AttachmentNotFoundException;
import pl.bartek537.snapdrop.features.share.exception.InvalidTokenException;
import pl.bartek537.snapdrop.features.share.exception.ShareExpiredException;
import pl.bartek537.snapdrop.features.share.exception.ShareNotFoundException;
import pl.bartek537.snapdrop.features.share.model.Attachment;
import pl.bartek537.snapdrop.features.share.model.Share;
import pl.bartek537.snapdrop.features.share.repository.AttachmentRepository;
import pl.bartek537.snapdrop.features.share.repository.ShareRepository;
import pl.bartek537.snapdrop.features.share.repository.StorageRepository;

import java.time.Clock;
import java.util.UUID;

@Service
public class AttachmentService {

    private final PasswordEncoder tokenEncoder = new BCryptPasswordEncoder();

    private final Clock clock;

    private final AttachmentRepository attachmentRepository;
    private final ShareRepository shareRepository;

    private final StorageRepository storageRepository;

    public AttachmentService(Clock clock, AttachmentRepository attachmentRepository, ShareRepository shareRepository, StorageRepository storageRepository) {
        this.clock = clock;
        this.attachmentRepository = attachmentRepository;
        this.shareRepository = shareRepository;
        this.storageRepository = storageRepository;
    }

    @Transactional
    public Attachment storeAttachment(UUID shareId, String token, MultipartFile file) {
        // TODO: (optionally) Limit the number of files that can be uploaded.
        // TODO: Check for closed state.

        Share share = shareRepository.findById(shareId) //
                .orElseThrow(() -> new ShareNotFoundException(shareId));

        if (!tokenEncoder.matches(token, share.getToken())) {
            throw new InvalidTokenException();
        }
        if (share.isExpired(clock)) {
            throw new ShareExpiredException(shareId);
        }

        Attachment attachment = new Attachment();
        attachment.setFileName(file.getOriginalFilename());
        share.addAttachment(attachment);

        attachment = attachmentRepository.save(attachment);

        storageRepository.save(file, attachment.getId().toString());

        // FIXME: Technically it's possible to be still uploading files after the share expired or was closed.

        return attachment;
    }

    public Attachment getAttachmentById(UUID attachmentId, UUID shareId) {
        return attachmentRepository.findByIdAndShareId(attachmentId, shareId) //
                .map(this::assertNotExpired).orElseThrow(() -> new AttachmentNotFoundException(attachmentId, shareId));
    }

    public AttachmentDownload prepareAttachmentDownload(UUID attachmentId, UUID shareId) {
        Attachment attachment = attachmentRepository.findByIdAndShareId(attachmentId, shareId)
                .map(this::assertNotExpired).orElseThrow(() -> new AttachmentNotFoundException(attachmentId, shareId));

        Resource file = storageRepository.loadAsResource(attachmentId.toString());
        return new AttachmentDownload(file, attachment.getFileName());
    }

    private Attachment assertNotExpired(Attachment attachment) {
        Share share = attachment.getShare();

        if (share.isExpired(clock)) {
            throw new ShareExpiredException(share.getId());
        }
        return attachment;
    }
}
