package pl.bartek537.snapdrop.web;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfiguration implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/uploads").setViewName("forward:/uploads.html");
        registry.addViewController("/uploads/*").setViewName("forward:/uploads.html");
        registry.addViewController("/downloads").setViewName("forward:/downloads.html");
    }
}
