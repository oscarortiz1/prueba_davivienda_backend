package com.davivienda.survey.presentation.controller;

import com.davivienda.survey.application.service.ImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class ImageController {
    
    private final ImageService imageService;
    
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            String imageUrl = imageService.saveImage(file);
            
            Map<String, String> response = new HashMap<>();
            response.put("imageUrl", imageUrl);
            response.put("message", "Imagen subida exitosamente");
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.error("Error de validación al subir imagen", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (IOException e) {
            log.error("Error al guardar imagen", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al guardar la imagen"));
        }
    }
    
    @PostMapping("/upload-base64")
    public ResponseEntity<?> uploadImageBase64(@RequestBody Map<String, String> request) {
        try {
            String base64Image = request.get("image");
            if (base64Image == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Campo 'image' requerido"));
            }
            
            String imageUrl = imageService.saveImageBase64(base64Image);
            
            Map<String, String> response = new HashMap<>();
            response.put("imageUrl", imageUrl);
            response.put("message", "Imagen subida exitosamente");
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.error("Error de validación al subir imagen base64", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (IOException e) {
            log.error("Error al guardar imagen base64", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al guardar la imagen"));
        }
    }
    
    @GetMapping("/{filename}")
    public ResponseEntity<byte[]> getImage(@PathVariable String filename) {
        try {
            byte[] imageBytes = imageService.getImage(filename);
            
            MediaType mediaType = MediaType.IMAGE_JPEG;
            if (filename.endsWith(".png")) {
                mediaType = MediaType.IMAGE_PNG;
            } else if (filename.endsWith(".gif")) {
                mediaType = MediaType.IMAGE_GIF;
            } else if (filename.endsWith(".webp")) {
                mediaType = MediaType.valueOf("image/webp");
            }
            
            return ResponseEntity.ok()
                    .contentType(mediaType)
                    .body(imageBytes);
        } catch (IOException e) {
            log.error("Error al obtener imagen", e);
            return ResponseEntity.notFound().build();
        }
    }
}
