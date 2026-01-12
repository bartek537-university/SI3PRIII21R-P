package pl.bartek537.snapdrop.features.share;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import pl.bartek537.snapdrop.features.share.event.AttachmentDeletedEvent;
import pl.bartek537.snapdrop.features.share.repository.StorageRepository;

@Component
public class AttachmentCleanupTask {

    private static final Logger logger = LoggerFactory.getLogger(AttachmentCleanupTask.class);

    private final StorageRepository storageRepository;

    public AttachmentCleanupTask(StorageRepository storageRepository) {
        this.storageRepository = storageRepository;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void deleteAttachmentFile(AttachmentDeletedEvent event) {
        try {
            if (storageRepository.delete(event.attachmentId().toString())) {
                logger.info("Deleted attachment file: {}", event.attachmentId());
            }
        } catch (Exception e) {
            logger.error("Deleting file failed for attachment: {}", event.attachmentId(), e);
        }
    }
}
