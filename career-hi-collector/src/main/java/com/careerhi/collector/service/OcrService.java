package com.careerhi.collector.service;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@Service
public class OcrService {

    public String readImageText(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            Path tempFile = Files.createTempFile("ocr_target", ".png");
            try (var in = url.openStream()) {
                Files.copy(in, tempFile, StandardCopyOption.REPLACE_EXISTING);
            }

            ITesseract instance = new Tesseract();

            instance.setDatapath("D:\\Users\\Wang\\Desktop\\career-hi\\tessdata");

            instance.setLanguage("kor+eng");

            String result = instance.doOCR(tempFile.toFile());

            Files.deleteIfExists(tempFile);

            return result;

        } catch (Exception e) {
            System.out.println("❌ OCR 실패: " + e.getMessage());
            return "";
        }
    }
}