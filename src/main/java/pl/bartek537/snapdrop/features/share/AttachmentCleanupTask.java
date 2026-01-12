package pl.bartek537.snapdrop.features.share;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import pl.bartek537.snapdrop.features.share.event.AttachmentDeletedEvent;
import pl.bartek537.snapdrop.features.share.repository.AttachmentRepository;
import pl.bartek537.snapdrop.features.share.repository.StorageRepository;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

@Component
public class AttachmentCleanupTask {

    private static final Logger logger = LoggerFactory.getLogger(AttachmentCleanupTask.class);

    private final StorageRepository storageRepository;
    private final AttachmentRepository attachmentRepository;

    @Value("${storage.filesystem.upload-path}")
    private Path uploadPath;

    public AttachmentCleanupTask(StorageRepository storageRepository, AttachmentRepository attachmentRepository) {
        this.storageRepository = storageRepository;
        this.attachmentRepository = attachmentRepository;
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

    @Scheduled(cron = "${storage.cleanup.filesystem-cron}")
    public void deleteOrphanedUploads() {
        logger.info("Starting cleanup task for orphaned uploads...");
        AtomicInteger deletedCount = new AtomicInteger();

        try (Stream<Path> allPaths = Files.walk(uploadPath)) {
            Set<UUID> existingAttachmentIds = attachmentRepository.findAllIds();

            allPaths.filter(Files::isRegularFile) //
                    .filter(path -> isOrphanedUpload(path, existingAttachmentIds)) //
                    .forEach(path -> {
                        storageRepository.delete(path.getFileName().toString());
                        deletedCount.incrementAndGet();
                    });
            logger.info("Removed {} orphaned uploads.", deletedCount);
        } catch (Exception e) {
            logger.info("Cleanup task for orphaned uploads failed.", e);
        }
    }

    private boolean isOrphanedUpload(Path path, Set<UUID> existingAttachmentIds) {
        String fileName = path.getFileName().toString();

        try {
            UUID attachmentId = UUID.fromString(fileName);
            return !existingAttachmentIds.contains(attachmentId);
        } catch (IllegalArgumentException e) {
            return true;
        }
    }
}
