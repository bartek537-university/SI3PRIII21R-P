package pl.bartek537.snapdrop.features.share.repository;

import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import pl.bartek537.snapdrop.features.share.model.Share;

import java.time.Instant;
import java.util.UUID;

public interface ShareRepository extends JpaRepository<@NonNull Share, @NonNull UUID> {
    boolean existsBySlugAndExpiresAtAfter(String slug, Instant instant);

    int deleteAllByExpiresAtBefore(Instant now);
}
