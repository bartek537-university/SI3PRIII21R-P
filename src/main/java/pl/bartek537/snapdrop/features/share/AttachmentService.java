package pl.bartek537.snapdrop.features.share;

import jakarta.annotation.Nullable;
import jakarta.transaction.Transactional;
import org.springframework.core.io.Resource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pl.bartek537.snapdrop.features.share.dto.AttachmentDownload;
import pl.bartek537.snapdrop.features.share.exception.InvalidTokenException;
import pl.bartek537.snapdrop.features.share.exception.ShareClosedException;
import pl.bartek537.snapdrop.features.share.exception.ShareNotFoundException;
import pl.bartek537.snapdrop.features.share.exception.ShareNotReadyException;
import pl.bartek537.snapdrop.features.share.model.Attachment;
import pl.bartek537.snapdrop.features.share.model.Share;
import pl.bartek537.snapdrop.features.share.repository.AttachmentRepository;
import pl.bartek537.snapdrop.features.share.repository.ShareRepository;
import pl.bartek537.snapdrop.features.share.repository.StorageRepository;

import java.time.Clock;
import java.util.Set;
import java.util.UUID;

@Service
public class AttachmentService {

    private final PasswordEncoder tokenEncoder = new BCryptPasswordEncoder();

    private final Clock clock;

    private final AttachmentRepository attachmentRepository;
    private final ShareRepository shareRepository;

    private final StorageRepository storageRepository;

    public AttachmentService(Clock clock, AttachmentRepository attachmentRepository, ShareRepository shareRepository,
                             StorageRepository storageRepository) {
        this.clock = clock;
        this.attachmentRepository = attachmentRepository;
        this.shareRepository = shareRepository;
        this.storageRepository = storageRepository;
    }

    @Transactional
    public Attachment storeAttachment(UUID shareId, String token, MultipartFile file) {
        Share share = getValidShare(shareId);

        if (!tokenEncoder.matches(token, share.getToken())) {
            throw new InvalidTokenException();
        }
        if (!share.isOpen()) {
            throw new ShareClosedException(shareId);
        }

        // TODO: (optionally) Limit the number of files that can be uploaded.

        Attachment attachment = new Attachment();
        attachment.setFileName(file.getOriginalFilename());

        share.addAttachment(attachment);
        attachment = attachmentRepository.save(attachment);

        String attachmentFileName = attachment.getId().toString();
        storageRepository.save(file, attachmentFileName);

        // FIXME: Technically it's possible to be still uploading files after the share expired or was closed.

        return attachment;
    }

    public Set<Attachment> getAttachmentsByShareId(UUID shareId) {
        return getValidShare(shareId).getAttachments();
    }

    private Share getValidShare(UUID shareId) {
        return shareRepository.findById(shareId).filter(share -> !share.isExpired(clock))
                .orElseThrow(() -> new ShareNotFoundException(shareId));
    }

    public Attachment getAttachmentById(UUID attachmentId, UUID shareId) {
        return getValidAttachment(attachmentId, shareId);
    }

    public AttachmentDownload prepareAttachmentDownload(UUID attachmentId, UUID shareId, @Nullable String token) {
        Attachment attachment = getValidAttachment(attachmentId, shareId);
        Share share = attachment.getShare();

        if (!share.isOpen() || tokenEncoder.matches(token, share.getToken())) {
            Resource file = storageRepository.loadAsResource(attachmentId.toString());
            return new AttachmentDownload(file, attachment.getFileName());
        }

        if (share.isOpen()) {
            throw new InvalidTokenException();
        }
        throw new ShareNotReadyException(shareId);
    }

    public void deleteAttachmentById(UUID attachmentId, UUID shareId, String token) {
        Attachment attachment = getValidAttachment(attachmentId, shareId);
        Share share = attachment.getShare();

        if (!tokenEncoder.matches(token, share.getToken())) {
            throw new InvalidTokenException();
        }
        if (!share.isOpen()) {
            throw new ShareClosedException(shareId);
        }

        attachmentRepository.delete(attachment);
    }

    private Attachment getValidAttachment(UUID attachmentId, UUID shareId) {
        return attachmentRepository.findByIdAndShareId(attachmentId, shareId)
                .filter(attachment -> !attachment.getShare().isExpired(clock))
                .orElseThrow(() -> new ShareNotFoundException(shareId));
    }
}
