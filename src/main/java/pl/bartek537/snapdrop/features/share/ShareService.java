package pl.bartek537.snapdrop.features.share;

import org.springframework.stereotype.Service;
import pl.bartek537.snapdrop.features.share.model.Share;
import pl.bartek537.snapdrop.features.share.repository.ShareRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ShareService {

    private final ShareRepository repository;

    ShareService(ShareRepository repository) {
        this.repository = repository;
    }

    public Share createNewShare() {
        Share share = new Share();
        return repository.save(share);
    }

    public List<Share> getAllShares() {
        return repository.findAll();
    }

    public Optional<Share> getShareById(UUID shareId) {
        return repository.findById(shareId);
    }
}
