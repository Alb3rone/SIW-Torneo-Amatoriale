package it.siw.tornei.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

import java.nio.file.Paths;

/**
 * Configurazione web globale:
 *  - CORS per il modulo React (chiamate /rest/** dal browser)
 *  - Mappa /uploads/** alla cartella su disco dove FileStorageService salva i loghi
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.upload-dir:uploads}")
    private String uploadDir;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/rest/**")
                .allowedOrigins("http://localhost:5173")
                .allowedMethods("GET", "POST", "PUT", "DELETE");
    }

    /**
     * Espone i file caricati dagli admin come risorse statiche.
     * Quando il template Thymeleaf scrive <img src="/uploads/abc.png">, il browser
     * fa GET /uploads/abc.png e Spring lo serve direttamente dalla cartella su disco.
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String location = "file:" + Paths.get(uploadDir).toAbsolutePath().normalize() + "/";
        registry.addResourceHandler("/uploads/**").addResourceLocations(location);
    }
}
