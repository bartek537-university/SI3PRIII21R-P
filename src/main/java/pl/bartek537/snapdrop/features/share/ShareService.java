package pl.bartek537.snapdrop.features.share;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.jspecify.annotations.NonNull;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import pl.bartek537.snapdrop.features.share.dto.ShareCreateRequest;
import pl.bartek537.snapdrop.features.share.dto.SharePatchRequest;
import pl.bartek537.snapdrop.features.share.dto.ShareResponse;
import pl.bartek537.snapdrop.features.share.exception.InvalidTokenException;
import pl.bartek537.snapdrop.features.share.exception.ShareNotFoundException;
import pl.bartek537.snapdrop.features.share.model.Share;
import pl.bartek537.snapdrop.features.share.repository.ShareRepository;

import java.time.Clock;
import java.util.List;
import java.util.UUID;

@Service
@Validated
public class ShareService {

    private final PasswordEncoder tokenEncoder = new BCryptPasswordEncoder();

    private final Clock clock;

    private final ShareRepository shareRepository;

    ShareService(Clock clock, ShareRepository shareRepository) {
        this.clock = clock;
        this.shareRepository = shareRepository;
    }

    @Transactional
    public ShareResponse createNewShare(@NonNull @Valid ShareCreateRequest request) {
        String token = KeyGenerators.string().generateKey();

        Share share = new Share(tokenEncoder.encode(token));
        share.setExpiresAt(request.expiresAt(), clock);
        // TODO: Validate and set the slug.

        share = shareRepository.save(share);

        return new ShareResponse(share, token);
    }

    // For development purposes only.
    public List<Share> getAllShares() {
        return shareRepository.findAll();
    }

    public Share getShareById(UUID shareId) {
        return shareRepository.findById(shareId).orElseThrow(() -> new ShareNotFoundException(shareId));
    }

    @Transactional
    public Share patchShareById(UUID shareId, String token, @NonNull @Valid SharePatchRequest request) {
        Share share = shareRepository.findById(shareId) //
                .orElseThrow(() -> new ShareNotFoundException(shareId));

        if (!tokenEncoder.matches(token, share.getToken())) {
            throw new InvalidTokenException();
        }

        if (request.expiresAt() != null) {
            share.setExpiresAt(request.expiresAt(), clock);
        }
        // TODO: Validate and set the slug.

        return shareRepository.save(share);
    }

    @Transactional
    public void deleteShareById(UUID shareId, String token) {
        Share share = shareRepository.findById(shareId) //
                .orElseThrow(() -> new ShareNotFoundException(shareId));

        if (!tokenEncoder.matches(token, share.getToken())) {
            throw new InvalidTokenException();
        }
        shareRepository.deleteById(shareId);
    }
}
