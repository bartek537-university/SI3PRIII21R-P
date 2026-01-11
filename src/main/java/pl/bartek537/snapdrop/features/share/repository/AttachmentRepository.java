package pl.bartek537.snapdrop.features.share.repository;

import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import pl.bartek537.snapdrop.features.share.model.Attachment;

import java.util.UUID;

public interface AttachmentRepository extends JpaRepository<@NonNull Attachment, @NonNull UUID> {
}
