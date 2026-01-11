package pl.bartek537.snapdrop.features.share;

import org.springframework.stereotype.Service;
import pl.bartek537.snapdrop.features.share.model.Share;
import pl.bartek537.snapdrop.features.share.repository.ShareRepository;

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
}
