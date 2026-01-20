package org.example.imageeditor.service;

import java.io.IOException;

public interface ImageService {
    void drawTitleBox(
            String inputPath,
            String outputPath,
            int number,
            String title
    ) throws IOException;
}
