package pl.bartek537.snapdrop.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import pl.bartek537.snapdrop.features.share.model.Attachment;
import pl.bartek537.snapdrop.features.share.model.Share;

import java.util.Set;

@Controller
public class WebController {

    private final WebService webService;

    public WebController(WebService webService) {
        this.webService = webService;
    }

    @GetMapping("/{slug:(?!uploads|downloads|static|api|index.html|favicon.ico).+}")
    public String handleShortLink(@PathVariable String slug) {
        Share share = webService.getShareBySlug(slug);
        Set<Attachment> attachments = share.getAttachments();

        if (attachments.size() != 1) {
            return String.format("redirect:/downloads/%s", share.getId());
        }
        Attachment attachment = attachments.iterator().next();
        return String.format("redirect:/api/shares/%s/attachments/%s/file", share.getId(), attachment.getId());
    }
}
