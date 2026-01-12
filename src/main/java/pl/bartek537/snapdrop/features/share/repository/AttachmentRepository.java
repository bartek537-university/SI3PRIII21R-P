package pl.bartek537.snapdrop.features.share.repository;

import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pl.bartek537.snapdrop.features.share.model.Attachment;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface AttachmentRepository extends JpaRepository<@NonNull Attachment, @NonNull UUID> {
    Optional<Attachment> findByIdAndShareId(UUID attachmentId, UUID shareId);

    @Query("SELECT a.id FROM Attachment a")
    Set<UUID> findAllIds();
}
