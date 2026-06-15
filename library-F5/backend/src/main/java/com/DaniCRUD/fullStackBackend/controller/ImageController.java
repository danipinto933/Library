package com.DaniCRUD.fullStackBackend.controller; // ¡Ajusta tu paquete!

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
public class ImageController {

    // Volvemos a usar la variable mágica para que te funcione en local y en Docker
    @Value("${IMAGE_PATH:uploads/covers/}")
    private String imagePath;

    // El ":.+" es muy importante para que lea la extensión (.jpg, .png)
    @GetMapping("/uploads/covers/{filename:.+}")
    public ResponseEntity<Resource> serveImage(@PathVariable String filename) {
        try {
            // Construye la ruta exacta a la foto
            Path filePath = Paths.get(imagePath).resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            // Si la encuentra y tiene permiso para leerla
            if (resource.exists() && resource.isReadable()) {
                String contentType = Files.probeContentType(filePath);
                if (contentType == null) contentType = "image/jpeg";

                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .body(resource);
            } else {
                // ESTA ES LA CLAVE: Si falla, nos chivará en la consola de Docker dónde ha buscado exactamente
                System.out.println("❌ ERROR: No se encontró la imagen en: " + filePath.toAbsolutePath());
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}