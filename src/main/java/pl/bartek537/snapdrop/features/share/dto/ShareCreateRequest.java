package pl.bartek537.snapdrop.features.share.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.Instant;

public record ShareCreateRequest(
        @Size(min = 6, max = 32, message = "Slug must be between 6 and 32 characters") @Pattern(regexp = "^[a-z0-9]+(?:-[a-z0-9]+)*$", message = "Slug must contain only lowercase letters, numbers and hyphens") String slug,
        Instant expiresAt) {
}
