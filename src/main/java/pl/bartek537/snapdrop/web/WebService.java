package pl.bartek537.snapdrop.web;

import org.springframework.stereotype.Service;
import pl.bartek537.snapdrop.features.share.exception.ShareNotFoundException;
import pl.bartek537.snapdrop.features.share.model.Share;
import pl.bartek537.snapdrop.features.share.repository.ShareRepository;

import java.time.Clock;

@Service
public class WebService {

    private final Clock clock;

    private final ShareRepository shareRepository;

    public WebService(Clock clock, ShareRepository shareRepository) {
        this.clock = clock;
        this.shareRepository = shareRepository;
    }

    public Share getShareBySlug(String slug) {
        return shareRepository.findBySlug(slug)
                .filter(share -> !share.isExpired(clock))
                .orElseThrow(() -> new ShareNotFoundException(slug));
    }
}
