package pl.bartek537.snapdrop.features.share.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
