package pl.bartek537.snapdrop.features.share.repository;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface StorageRepository {

    void save(MultipartFile file, String fileName);

    Resource loadAsResource(String fileName);
}
