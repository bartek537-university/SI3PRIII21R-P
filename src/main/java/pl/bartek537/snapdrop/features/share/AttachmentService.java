package pl.bartek537.snapdrop.features.share;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pl.bartek537.snapdrop.features.share.exception.ShareNotFoundException;
import pl.bartek537.snapdrop.features.share.model.Attachment;
import pl.bartek537.snapdrop.features.share.model.Share;
import pl.bartek537.snapdrop.features.share.repository.AttachmentRepository;
import pl.bartek537.snapdrop.features.share.repository.ShareRepository;
import pl.bartek537.snapdrop.features.share.repository.StorageRepository;

import java.util.UUID;

@Service
public class AttachmentService {

    private final AttachmentRepository attachmentRepository;
    private final ShareRepository shareRepository;

    private final StorageRepository storageRepository;

    public AttachmentService(AttachmentRepository attachmentRepository, ShareRepository shareRepository, StorageRepository storageRepository) {
        this.attachmentRepository = attachmentRepository;
        this.shareRepository = shareRepository;
        this.storageRepository = storageRepository;
    }

    @Transactional
    public Attachment storeAttachment(UUID shareId, MultipartFile file) {
        // TODO: Authenticate and authorize the user.

        Share share = shareRepository.findById(shareId).orElseThrow(() -> new ShareNotFoundException(shareId));

        Attachment attachment = new Attachment();
        attachment.setFileName(file.getOriginalFilename());
        share.addAttachment(attachment);

        attachment = attachmentRepository.save(attachment);

        storageRepository.save(file, attachment.getId().toString());

        return attachment;
    }
}
