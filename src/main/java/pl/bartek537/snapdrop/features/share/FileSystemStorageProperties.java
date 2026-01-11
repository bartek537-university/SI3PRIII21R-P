package pl.bartek537.snapdrop.features.share;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;

@Configuration
@ConfigurationProperties(prefix = "storage.filesystem")
public class FileSystemStorageProperties {
    private Path uploadPath;

    public Path getUploadPath() {
        return uploadPath;
    }

    public void setUploadPath(Path uploadPath) {
        this.uploadPath = uploadPath;
    }
}
