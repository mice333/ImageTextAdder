package ru.nokisev.ImageTextAdder.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.nokisev.ImageTextAdder.model.ImageDetails;
import ru.nokisev.ImageTextAdder.services.S3Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;

@RestController
@RequestMapping("/api/image")
public class ImageController {

    @Autowired
    private S3Service s3Service;

    @PostMapping("/new")
    public ResponseEntity<?> saveImage(@RequestBody ImageDetails imageDetails) {

        try {
            // 1. Загружаем изображение
            BufferedImage image = ImageIO.read(
                    new URL("https://upload.wikimedia.org/wikipedia/commons/thumb/e/ee/Flag_of_Bundi.svg/680px-Flag_of_Bundi.svg.png") // 680x432
            );

            // 2. Создаём копию изображения с поддержкой RGB (на случай, если исходное в неправильном формате)
            BufferedImage newImage = new BufferedImage(
                    image.getWidth(),
                    image.getHeight(),
                    BufferedImage.TYPE_INT_RGB
            );
            Graphics2D g2d = newImage.createGraphics();
            g2d.drawImage(image, 0, 0, null);

            // 3. Настройки рендеринга (для чёткого текста)
            g2d.setRenderingHint(
                    RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON
            );

            // 4. Шрифт (Arial, жирный, 36px)
            Font font = new Font("Arial", Font.BOLD, 36);
            g2d.setFont(font);

            // 6. Рисуем ЧЁРНУЮ обводку (4 смещения для "толстой" обводки)
            g2d.setColor(Color.BLACK);
            g2d.drawString(imageDetails.getTitle(), 25, 100);  // смещение вправо-вниз
            // смещение вправо-вниз
            g2d.drawString(imageDetails.getCreatedAt(), 490, 400);  // смещение вправо-вниз
            g2d.setFont(new Font("Arial", Font.ITALIC, 24));
            g2d.drawString(imageDetails.getDescription(), 25, 250);  // смещение вправо-вниз
//            g2d.setFont(new Font("Arial", Font.ITALIC, 24));
            g2d.drawString(imageDetails.getPriority(), 25, 175);

            // 8. Освобождаем ресурсы
            g2d.dispose();

            System.out.println(s3Service.returnAllBucketsResponse());

            // 9. Сохраняем (лучше в PNG!)
            ImageIO.write(newImage, "png", new File("result.png"));
            s3Service.saveFileToBucket(new File("result.png"));

            return ResponseEntity.ok("✅ Готово! Проверьте файл result.png");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("❌ Ошибка: " + e.getMessage());
        }
    }

}
