package ru.nokisev.ImageTextAdder.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.nokisev.ImageTextAdder.model.ImageDetails;
import ru.nokisev.ImageTextAdder.services.ImageService;
import ru.nokisev.ImageTextAdder.services.S3Service;

@Slf4j
@RestController
@RequestMapping("/image")
@RequiredArgsConstructor
public class ImageController {

    private final S3Service s3Service;
    private final ImageService imageService;


    @PostMapping("/new")
    public ResponseEntity<?> saveImage(@RequestBody ImageDetails imageDetails) {
        log.info("Использован контроллер для создания изображения \"/image/new\"");
        try {
            imageService.saveImage(imageDetails);
            return ResponseEntity.status(201).body("✅ Готово! Проверьте файл result.png");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("❌ Ошибка: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getImageLink(@PathVariable String id) {
        log.info("Использован контроллер для получения изображения \"/image/{}\"", id);
        return ResponseEntity.ok(s3Service.getImageLink(id));
    }

}
