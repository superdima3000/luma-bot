package org.example.test;

import org.example.imageeditor.service.ImageService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class ImageServiceTestIT {
    @TempDir
    Path tempDir;

    @Autowired
    private ImageService imageService;

    @Test
    void drawTitleBox_createsImageWithWhiteSquare() throws IOException {
        // 1. Подготовка: создаём простое исходное изображение 200x200 красного цвета
        BufferedImage inputImage = new BufferedImage(200, 200, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = inputImage.createGraphics();
        try {
            g2d.setColor(Color.RED);
            g2d.fillRect(0, 0, 200, 200);
        } finally {
            g2d.dispose();
        }

        File inputFile = tempDir.resolve("input.png").toFile();
        ImageIO.write(inputImage, "png", inputFile); // [web:23][web:16]

        File outputFile = tempDir.resolve("output.png").toFile();

        // 2. Вызываем тестируемый метод
        ImageTitleWriter.drawTitleBox(
                inputFile.getAbsolutePath(),
                outputFile.getAbsolutePath(),
                1,
                "тестовая вещь"
        );

        // 3. Проверяем, что файл создан
        assertTrue(outputFile.exists(), "Выходной файл не создан"); // [web:27][web:33]

        // 4. Загружаем результат и проверяем несколько пикселей в левом верхнем углу
        BufferedImage result = ImageIO.read(outputFile); // [web:23][web:16]

        // Пара пикселей, которые должны быть в белом квадрате 80x80
        int rgb00 = result.getRGB(0, 0);
        int rgb10 = result.getRGB(10, 10);
        int rgb50 = result.getRGB(50, 50);

        assertEquals(Color.WHITE.getRGB(), rgb00, "Пиксель (0,0) должен быть белым");
        assertEquals(Color.WHITE.getRGB(), rgb10, "Пиксель (10,10) должен быть белым");
        assertEquals(Color.WHITE.getRGB(), rgb50, "Пиксель (50,50) должен быть белым");

        // И для надёжности проверим пиксель за пределами квадрата,
        // что он остался не белым (например, красным как фон)
        int rgbOutside = result.getRGB(100, 100);
        assertNotEquals(Color.WHITE.getRGB(), rgbOutside, "Пиксель за пределами квадрата не должен быть белым");
    }
}
