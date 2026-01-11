package pl.bartek537.snapdrop.features.share.dto;

import org.springframework.core.io.Resource;

public record AttachmentDownload(Resource resource, String fileName) {
}
