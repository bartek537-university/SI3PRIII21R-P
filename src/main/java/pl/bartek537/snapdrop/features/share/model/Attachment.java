package pl.bartek537.snapdrop.features.share.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table
public class Attachment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String fileName;

    @ManyToOne
    @JoinColumn(nullable = false)
    @JsonIgnoreProperties("attachments")
    private Share share;

    public UUID getId() {
        return this.id;
    }

    public String getFileName() {
        return this.fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Share getShare() {
        return this.share;
    }

    public void setShare(Share share) {
        this.share = share;
    }
}
