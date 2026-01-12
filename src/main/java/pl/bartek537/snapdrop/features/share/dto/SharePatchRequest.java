package pl.bartek537.snapdrop.features.share.dto;

import java.time.Instant;

public record SharePatchRequest(Instant expiresAt) {
}
