package pl.bartek537.snapdrop.features.share;

import org.jspecify.annotations.NonNull;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.bartek537.snapdrop.features.share.dto.ShareResponse;
import pl.bartek537.snapdrop.features.share.model.Share;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("shares")
public class ShareController {

    private final ShareService shareService;

    ShareController(ShareService shareService) {
        this.shareService = shareService;
    }

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public ShareResponse createNewShare() {
        return shareService.createNewShare();
    }

    @GetMapping
    @Profile("dev")
    public List<Share> getAllShares() {
        return shareService.getAllShares();
    }

    @GetMapping("{shareId}")
    public ResponseEntity<@NonNull Share> getShareById(@PathVariable UUID shareId) {
        return shareService.getShareById(shareId).map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
