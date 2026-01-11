package pl.bartek537.snapdrop.features.share;

import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;
import pl.bartek537.snapdrop.features.share.repository.StorageRepository;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@Repository
public class FileSystemStorageRepository implements StorageRepository {

    private final FileSystemStorageProperties properties;

    public FileSystemStorageRepository(FileSystemStorageProperties properties) {
        this.properties = properties;
    }

    @Override
    public void save(MultipartFile file, String fileName) {
        Path destination = resolveFilePath(fileName);

        try (InputStream fileInputStream = file.getInputStream()) {
            Files.copy(fileInputStream, destination);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private Path resolveFilePath(String fileName) {
        return properties.getUploadPath().resolve(fileName);
    }
}
