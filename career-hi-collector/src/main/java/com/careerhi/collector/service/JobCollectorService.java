package com.careerhi.collector.service;

import net.sourceforge.tess4j.Tesseract;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;

public class JobCollectorService { // 클래스 이름은 기존 거 쓰셔도 됩니다

    public String readImageText(String imageUrl) {
        try {
            Tesseract tesseract = new Tesseract();

            File rootPath = new File(".");
            String tessDataPath = rootPath.getCanonicalPath() + "/tessdata";

            if (!new File(tessDataPath).exists()) {
                tessDataPath = rootPath.getCanonicalPath() + "/../tessdata";
            }

            File tessFolder = new File(tessDataPath);
            System.out.println("Tessdata 경로 확인: " + tessFolder.getAbsolutePath());

            if (!tessFolder.exists()) {
                return "❌ 오류: tessdata 폴더가 없습니다! 위치: " + tessFolder.getAbsolutePath();
            }

            tesseract.setDatapath(tessFolder.getAbsolutePath());
            tesseract.setLanguage("kor");

            URL url = new URL(imageUrl);
            BufferedImage image = ImageIO.read(url);

            if (image == null) return "❌ 오류: 이미지를 못 읽었습니다.";

            return tesseract.doOCR(image);

        } catch (Exception e) {
            e.printStackTrace();
            return "❌ OCR 에러 발생: " + e.getMessage();
        }
    }
}