package pl.bartek537.snapdrop.features.share;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.bartek537.snapdrop.features.share.model.Share;

@RestController
@RequestMapping("shares")
public class ShareController {

    private final ShareService shareService;

    ShareController(ShareService shareService) {
        this.shareService = shareService;
    }

    @PostMapping
    public Share createNewShare() {
        return shareService.createNewShare();
    }
}
