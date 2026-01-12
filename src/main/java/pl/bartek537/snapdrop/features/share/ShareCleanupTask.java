package pl.bartek537.snapdrop.features.share;

import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pl.bartek537.snapdrop.features.share.repository.ShareRepository;

import java.time.Clock;
import java.time.Instant;

@Component
public class ShareCleanupTask {

    private static final Logger logger = LoggerFactory.getLogger(ShareCleanupTask.class);

    private final Clock clock;

    private final ShareRepository shareRepository;

    public ShareCleanupTask(Clock clock, ShareRepository shareRepository) {
        this.clock = clock;
        this.shareRepository = shareRepository;
    }

    @Transactional
    @Scheduled(fixedDelayString = "${storage.cleanup.database-delay}")
    public void deleteExpiredShares() {
        logger.info("Starting cleanup task for expired shares...");
        try {
            int deletedCount = shareRepository.deleteAllByExpiresAtBefore(Instant.now(clock));
            logger.info("Removed {} expired tasks.", deletedCount);
        } catch (Exception e) {
            logger.error("Cleanup task for expired shares failed.", e);
        }
    }
}
