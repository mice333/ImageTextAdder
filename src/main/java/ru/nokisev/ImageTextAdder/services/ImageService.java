package ru.nokisev.ImageTextAdder.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.nokisev.ImageTextAdder.model.ImageDetails;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;

@Slf4j
@Service
public class ImageService {

    @Autowired
    private S3Service s3Service;

    public void saveImage(ImageDetails imageDetails) throws IOException, FontFormatException {
        log.info("{}", imageDetails);

        imageDetails.setCreatedAt(LocalDate.now().toString());
        log.info("CreatedAt изменён: {}", imageDetails);
        imageSettings(imageDetails);
    }

    private void imageSettings(ImageDetails imageDetails) throws IOException, FontFormatException {
        try {
            log.info("Изображение настраивается");
            BufferedImage image = ImageIO.read(getClass().getResourceAsStream("/static/images/background.png"));

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
            InputStream in = getClass().getResourceAsStream("/static/JetBrainsMono-Bold.ttf");
            Font font = Font.createFont(Font.TRUETYPE_FONT, in).deriveFont(36f);
            g2d.setFont(font);

            // 6. Рисуем ЧЁРНУЮ обводку (4 смещения для "толстой" обводки)
            g2d.setColor(Color.BLACK);
            g2d.drawString(imageDetails.getTitle(), 25, 100);  // смещение вправо-вниз
            // смещение вправо-вниз
            g2d.drawString(imageDetails.getCreatedAt(), 430, 400);  // смещение вправо-вниз
            in = getClass().getResourceAsStream("/static/JetBrainsMono-Regular.ttf");
            font = Font.createFont(Font.TRUETYPE_FONT, in).deriveFont(Font.ITALIC,24f);
            g2d.setFont(font);
            g2d.drawString(imageDetails.getDescription(), 25, 175);  // смещение вправо-вниз
            g2d.drawString(imageDetails.getPriority(), 25, 250);

            // 8. Освобождаем ресурсы
            in.close();
            g2d.dispose();

            log.info("Изменения сохранены");
            ImageIO.write(newImage, "png", new File("result.png"));
            log.info("Изменения отправляются в S3");
            s3Service.saveFileToBucket(new File("result.png"), imageDetails.getId());
        } catch (Exception e) {
            log.error(e.getMessage());
        }

    }
}
