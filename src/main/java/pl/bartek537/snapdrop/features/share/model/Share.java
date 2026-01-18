package pl.bartek537.snapdrop.features.share.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import pl.bartek537.snapdrop.features.share.exception.InvalidExpirationDateException;
import pl.bartek537.snapdrop.features.share.exception.ShareClosedException;

import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(indexes = @Index(columnList = "slug"))
public class Share {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String slug;

    private boolean isOpen;

    @CreationTimestamp
    private Instant createdAt;

    private Instant expiresAt;

    @JsonIgnore
    private String token;

    @OneToMany(mappedBy = "share", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("share")
    private Set<Attachment> attachments;

    public Share() {
    }

    public Share(String token) {
        this.token = token;
    }

    public UUID getId() {
        return this.id;
    }

    public String getSlug() {
        return this.slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public boolean isOpen() {
        return this.isOpen;
    }

    public void setOpen(boolean isOpen) {
        if (!this.isOpen && isOpen) {
            throw new ShareClosedException(this.id);
        }
        this.isOpen = isOpen;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Instant instant, Clock clock) {
        Instant minimum = this.createdAt != null ? this.createdAt : Instant.now(clock);
        Instant maximum = minimum.plus(24, ChronoUnit.HOURS);

        if (instant == null) {
            this.expiresAt = maximum;
        } else if (instant.isBefore(minimum) || instant.isAfter(maximum)) {
            throw new InvalidExpirationDateException(instant);
        } else {
            this.expiresAt = instant;
        }
    }

    public boolean isExpired(Clock clock) {
        Instant now = Instant.now(clock);
        return this.expiresAt.isBefore(now);
    }

    public String getToken() {
        return token;
    }

    public Set<Attachment> getAttachments() {
        return this.attachments;
    }

    public void addAttachment(Attachment attachment) {
        this.attachments.add(attachment);
        attachment.setShare(this);
    }
}
