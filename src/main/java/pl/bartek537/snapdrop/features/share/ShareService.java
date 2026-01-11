package pl.bartek537.snapdrop.features.share;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.bartek537.snapdrop.features.share.dto.ShareResponse;
import pl.bartek537.snapdrop.features.share.exception.InvalidTokenException;
import pl.bartek537.snapdrop.features.share.exception.ShareNotFoundException;
import pl.bartek537.snapdrop.features.share.model.Share;
import pl.bartek537.snapdrop.features.share.repository.ShareRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ShareService {

    private final PasswordEncoder tokenEncoder = new BCryptPasswordEncoder();

    private final ShareRepository shareRepository;

    ShareService(ShareRepository shareRepository) {
        this.shareRepository = shareRepository;
    }

    public ShareResponse createNewShare() {
        String token = KeyGenerators.string().generateKey();
        Share share = shareRepository.save(new Share(tokenEncoder.encode(token)));

        return new ShareResponse(share, token);
    }

    public List<Share> getAllShares() {
        return shareRepository.findAll();
    }

    public Optional<Share> getShareById(UUID shareId) {
        return shareRepository.findById(shareId);
    }

    public void deleteShareById(UUID shareId, String token) {
        Share share = shareRepository.findById(shareId).orElseThrow(() -> new ShareNotFoundException(shareId));

        if (!tokenEncoder.matches(token, share.getToken())) {
            throw new InvalidTokenException();
        }

        shareRepository.deleteById(shareId);
    }
}
