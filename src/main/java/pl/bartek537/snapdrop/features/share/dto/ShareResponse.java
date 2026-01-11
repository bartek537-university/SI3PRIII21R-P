package pl.bartek537.snapdrop.features.share.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import pl.bartek537.snapdrop.features.share.model.Share;

public record ShareResponse(Share share, @JsonInclude(JsonInclude.Include.NON_NULL) String token) {
}
