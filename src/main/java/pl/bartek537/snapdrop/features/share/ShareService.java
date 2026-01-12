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
import pl.bartek537.snapdrop.features.share.exception.SlugExistsException;
import pl.bartek537.snapdrop.features.share.exception.SlugGenerationException;
import pl.bartek537.snapdrop.features.share.model.Share;
import pl.bartek537.snapdrop.features.share.repository.ShareRepository;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@Validated
public class ShareService {

    private static final int MAX_SLUG_GENERATION_RETRIES = 5;

    private final PasswordEncoder tokenEncoder = new BCryptPasswordEncoder();

    private final Clock clock;

    private final SlugService slugService;

    private final ShareRepository shareRepository;

    ShareService(Clock clock, SlugService slugService, ShareRepository shareRepository) {
        this.clock = clock;
        this.slugService = slugService;
        this.shareRepository = shareRepository;
    }

    @Transactional
    public ShareResponse createNewShare(@NonNull @Valid ShareCreateRequest request) {
        String token = KeyGenerators.string().generateKey();
        String slug = request.slug() != null ? request.slug() : generateUniqueSlug();

        if (shareRepository.existsBySlugAndExpiresAtAfter(slug, Instant.now(clock))) {
            throw new SlugExistsException(slug);
        }

        Share share = new Share(tokenEncoder.encode(token));
        share.setExpiresAt(request.expiresAt(), clock);
        share.setSlug(slug);

        share = shareRepository.save(share);

        return new ShareResponse(share, token);
    }

    private String generateUniqueSlug() {
        for (int i = 0; i < MAX_SLUG_GENERATION_RETRIES; i++) {
            String slug = slugService.generateSlug();

            if (shareRepository.existsBySlugAndExpiresAtAfter(slug, Instant.now(clock))) {
                continue;
            }
            return slug;
        }
        throw new SlugGenerationException();
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
