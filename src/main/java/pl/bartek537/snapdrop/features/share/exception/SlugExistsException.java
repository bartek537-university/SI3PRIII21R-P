package pl.bartek537.snapdrop.features.share.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class SlugExistsException extends RuntimeException {
    public SlugExistsException(String slug) {
        super("Slug already exists: " + slug);
    }
}
