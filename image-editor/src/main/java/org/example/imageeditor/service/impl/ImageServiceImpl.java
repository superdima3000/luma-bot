package org.example.imageeditor.service.impl;

import org.example.imageeditor.service.ImageService;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@Component
public class ImageServiceImpl implements ImageService {

    @Override
    public void drawTitleBox(
            String inputPath,
            String outputPath,
            int number,
            String title
    ) throws IOException {

        BufferedImage image = ImageIO.read(new File(inputPath));

        Graphics2D g2d = image.createGraphics();

        try {
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            int squareSize = 80;
            int x = 0;
            int y = 0;

            g2d.setColor(Color.WHITE);
            g2d.fillRect(x, y, squareSize, squareSize);

            g2d.setColor(Color.BLACK);
            g2d.setFont(new Font("SansSerif", Font.PLAIN, 16));

            String text = number + ". " + title;

            int textX = x + 8;
            int textY = y + 20;

            g2d.drawString(text, textX, textY);
        } finally {
            g2d.dispose();
        }

        ImageIO.write(image, "png", new File(outputPath));
    }
}
