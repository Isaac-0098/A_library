package cn.agehaa.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Value("${file.upload.url-prefix}")
    private String urlPrefix;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(urlPrefix + "**")
                .addResourceLocations("classpath:/imge/");
        
        registry.addResourceHandler("/photo/**")
                .addResourceLocations("classpath:/photo/")
                .setCachePeriod(0);
    }
} 