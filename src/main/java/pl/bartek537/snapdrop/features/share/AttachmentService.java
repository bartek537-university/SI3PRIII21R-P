package pl.bartek537.snapdrop.features.share;

import jakarta.transaction.Transactional;
import org.springframework.core.io.Resource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pl.bartek537.snapdrop.features.share.dto.AttachmentDownload;
import pl.bartek537.snapdrop.features.share.exception.InvalidTokenException;
import pl.bartek537.snapdrop.features.share.exception.ShareNotFoundException;
import pl.bartek537.snapdrop.features.share.model.Attachment;
import pl.bartek537.snapdrop.features.share.model.Share;
import pl.bartek537.snapdrop.features.share.repository.AttachmentRepository;
import pl.bartek537.snapdrop.features.share.repository.ShareRepository;
import pl.bartek537.snapdrop.features.share.repository.StorageRepository;

import java.util.Optional;
import java.util.UUID;

@Service
public class AttachmentService {

    private final PasswordEncoder tokenEncoder = new BCryptPasswordEncoder();

    private final AttachmentRepository attachmentRepository;
    private final ShareRepository shareRepository;

    private final StorageRepository storageRepository;

    public AttachmentService(AttachmentRepository attachmentRepository, ShareRepository shareRepository, StorageRepository storageRepository) {
        this.attachmentRepository = attachmentRepository;
        this.shareRepository = shareRepository;
        this.storageRepository = storageRepository;
    }

    @Transactional
    public Attachment storeAttachment(UUID shareId, String token, MultipartFile file) {
        // TODO: Limit the number of files that can be uploaded.

        Share share = shareRepository.findById(shareId).orElseThrow(() -> new ShareNotFoundException(shareId));

        if (!tokenEncoder.matches(token, share.getToken())) {
            throw new InvalidTokenException();
        }

        Attachment attachment = new Attachment();
        attachment.setFileName(file.getOriginalFilename());
        share.addAttachment(attachment);

        attachment = attachmentRepository.save(attachment);

        storageRepository.save(file, attachment.getId().toString());

        return attachment;
    }

    public Optional<Attachment> getAttachmentById(UUID attachmentId, UUID shareId) {
        return attachmentRepository.findByIdAndShareId(attachmentId, shareId);
    }

    public Optional<AttachmentDownload> prepareAttachmentDownload(UUID attachmentId, UUID shareId) {
        return getAttachmentById(attachmentId, shareId).map(attachment -> {
            Resource file = storageRepository.loadAsResource(attachmentId.toString());
            return new AttachmentDownload(file, attachment.getFileName());
        });
    }
}
