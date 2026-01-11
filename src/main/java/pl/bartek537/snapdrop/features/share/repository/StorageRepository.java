package pl.bartek537.snapdrop.features.share.repository;

import org.springframework.web.multipart.MultipartFile;

public interface StorageRepository {

    void save(MultipartFile file, String fileName);
}
