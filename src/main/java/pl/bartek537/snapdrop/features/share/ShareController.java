package pl.bartek537.snapdrop.features.share;

import jakarta.validation.Valid;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pl.bartek537.snapdrop.features.share.dto.ShareCreateRequest;
import pl.bartek537.snapdrop.features.share.dto.SharePatchRequest;
import pl.bartek537.snapdrop.features.share.dto.ShareResponse;
import pl.bartek537.snapdrop.features.share.model.Share;

import java.util.List;
import java.util.UUID;

import static pl.bartek537.snapdrop.Constants.MANAGEMENT_TOKEN_HEADER;

@RestController
@RequestMapping("shares")
public class ShareController {

    private final ShareService shareService;

    ShareController(ShareService shareService) {
        this.shareService = shareService;
    }

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public ShareResponse createNewShare(@Valid @RequestBody ShareCreateRequest request) {
        return shareService.createNewShare(request);
    }

    @GetMapping
    @Profile("dev")
    public List<Share> getAllShares() {
        return shareService.getAllShares();
    }

    @GetMapping("{shareId}")
    public Share getShareById(@PathVariable UUID shareId) {
        return shareService.getShareById(shareId);
    }

    @PatchMapping("{shareId}")
    public Share patchShareById(@PathVariable UUID shareId, @RequestHeader(MANAGEMENT_TOKEN_HEADER) String token, @Valid @RequestBody SharePatchRequest request) {
        return shareService.patchShareById(shareId, token, request);
    }

    @DeleteMapping("{shareId}")
    public void deleteShareById(@PathVariable UUID shareId, @RequestHeader(MANAGEMENT_TOKEN_HEADER) String token) {
        shareService.deleteShareById(shareId, token);
    }
}
