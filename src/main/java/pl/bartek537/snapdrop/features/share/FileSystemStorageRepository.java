package pl.bartek537.snapdrop.features.share;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.multipart.MultipartFile;
import pl.bartek537.snapdrop.features.share.repository.StorageRepository;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;

@Repository
public class FileSystemStorageRepository implements StorageRepository {

    @Value("${storage.filesystem.upload-path}")
    private Path uploadPath;

    // TODO: Optionally harden against file names that traverse through directories
    //  (should never happen, because they are constructed from UUIDs).

    @Override
    public void save(MultipartFile file, String fileName) {
        Path destination = resolveFilePath(fileName);

        try (InputStream fileInputStream = file.getInputStream()) {
            Files.copy(fileInputStream, destination);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Resource loadAsResource(String fileName) {
        try {
            Path source = resolveFilePath(fileName);
            Resource resource = new UrlResource(source.toUri());

            if (resource.isReadable()) {
                return resource;
            }
            throw new ResourceAccessException("Unable to access as resource: " + fileName);
        } catch (MalformedURLException e) {
            throw new ResourceAccessException("Invalid file path: " + fileName, e);
        }
    }

    @Override
    public boolean delete(String fileName) {
        Path destination = resolveFilePath(fileName);
        try {
            return Files.deleteIfExists(destination);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private Path resolveFilePath(String fileName) {
        return uploadPath.resolve(fileName);
    }
}
