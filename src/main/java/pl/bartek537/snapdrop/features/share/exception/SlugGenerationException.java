package pl.bartek537.snapdrop.features.share.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class SlugGenerationException extends RuntimeException {
    public SlugGenerationException() {
        super("Failed to generate unique slug");
    }
}
