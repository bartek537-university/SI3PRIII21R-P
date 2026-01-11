package pl.bartek537.snapdrop.features.share.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.util.Set;
import java.util.UUID;

@Entity
@Table
public class Share {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToMany(mappedBy = "share", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("share")
    private Set<Attachment> attachments;

    public UUID getId() {
        return this.id;
    }

    public Set<Attachment> getAttachments() {
        return this.attachments;
    }

    public void addAttachment(Attachment attachment) {
        this.attachments.add(attachment);
        attachment.setShare(this);
    }
}
