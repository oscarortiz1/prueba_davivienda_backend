package com.davivienda.survey.application.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.UUID;

@Service
@Slf4j
public class ImageService {
    
    private static final long MAX_FILE_SIZE = 2 * 1024 * 1024; 
    private static final String[] ALLOWED_EXTENSIONS = {"jpg", "jpeg", "png", "gif", "webp"};
    private static final String UPLOAD_DIR = "uploads/images/";
    
    public String saveImage(MultipartFile file) throws IOException {
        validateImage(file);
        
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        String originalFilename = file.getOriginalFilename();
        String extension = getFileExtension(originalFilename);
        String filename = UUID.randomUUID().toString() + "." + extension;
        
        Path filePath = uploadPath.resolve(filename);
        Files.copy(file.getInputStream(), filePath);
        
        log.info("✅ Imagen guardada: {}", filename);
        
        return "/api/images/" + filename;
    }
    
    public String saveImageBase64(String base64Image) throws IOException {
        validateBase64Image(base64Image);
        
        String[] parts = base64Image.split(",");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Formato de imagen base64 inválido");
        }
        
        String metadata = parts[0];
        String imageData = parts[1];
        
        String extension = extractExtensionFromMimeType(metadata);
        
        byte[] imageBytes = Base64.getDecoder().decode(imageData);
        
        if (imageBytes.length > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("La imagen excede el tamaño máximo de 2MB");
        }
        
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        String filename = UUID.randomUUID().toString() + "." + extension;
        Path filePath = uploadPath.resolve(filename);
        
        Files.write(filePath, imageBytes);
        
        log.info("✅ Imagen base64 guardada: {}", filename);
        
        return "/api/images/" + filename;
    }
    
    public byte[] getImage(String filename) throws IOException {
        Path filePath = Paths.get(UPLOAD_DIR).resolve(filename);
        
        if (!Files.exists(filePath)) {
            throw new IOException("Imagen no encontrada: " + filename);
        }
        
        return Files.readAllBytes(filePath);
    }
    
    public void deleteImage(String imageUrl) {
        try {
            if (imageUrl != null && imageUrl.startsWith("/api/images/")) {
                String filename = imageUrl.substring("/api/images/".length());
                Path filePath = Paths.get(UPLOAD_DIR).resolve(filename);
                Files.deleteIfExists(filePath);
                log.info("🗑️ Imagen eliminada: {}", filename);
            }
        } catch (IOException e) {
            log.error("Error al eliminar imagen", e);
        }
    }
    
    private void validateImage(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("El archivo está vacío");
        }
        
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("La imagen excede el tamaño máximo de 2MB");
        }
        
        String filename = file.getOriginalFilename();
        if (filename == null || !isValidExtension(filename)) {
            throw new IllegalArgumentException("Tipo de archivo no permitido. Permitidos: jpg, jpeg, png, gif, webp");
        }
    }
    
    private void validateBase64Image(String base64Image) {
        if (base64Image == null || base64Image.isEmpty()) {
            throw new IllegalArgumentException("Imagen base64 vacía");
        }
        
        if (!base64Image.startsWith("data:image/")) {
            throw new IllegalArgumentException("Formato de imagen base64 inválido");
        }
    }
    
    private boolean isValidExtension(String filename) {
        String extension = getFileExtension(filename).toLowerCase();
        for (String allowed : ALLOWED_EXTENSIONS) {
            if (allowed.equals(extension)) {
                return true;
            }
        }
        return false;
    }
    
    private String getFileExtension(String filename) {
        if (filename == null) {
            return "";
        }
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return "";
        }
        return filename.substring(lastDotIndex + 1).toLowerCase();
    }
    
    private String extractExtensionFromMimeType(String metadata) {
        if (metadata.contains("image/jpeg") || metadata.contains("image/jpg")) {
            return "jpg";
        } else if (metadata.contains("image/png")) {
            return "png";
        } else if (metadata.contains("image/gif")) {
            return "gif";
        } else if (metadata.contains("image/webp")) {
            return "webp";
        }
        throw new IllegalArgumentException("Tipo de imagen no soportado");
    }
}
