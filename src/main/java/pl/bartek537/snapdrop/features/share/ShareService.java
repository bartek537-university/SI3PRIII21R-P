package pl.bartek537.snapdrop.features.share;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.bartek537.snapdrop.features.share.dto.ShareResponse;
import pl.bartek537.snapdrop.features.share.model.Share;
import pl.bartek537.snapdrop.features.share.repository.ShareRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ShareService {

    private final PasswordEncoder tokenEncoder = new BCryptPasswordEncoder();

    private final ShareRepository repository;

    ShareService(ShareRepository repository) {
        this.repository = repository;
    }

    public ShareResponse createNewShare() {
        String token = KeyGenerators.string().generateKey();
        Share share = repository.save(new Share(tokenEncoder.encode(token)));

        return new ShareResponse(share, token);
    }

    public List<Share> getAllShares() {
        return repository.findAll();
    }

    public Optional<Share> getShareById(UUID shareId) {
        return repository.findById(shareId);
    }
}
