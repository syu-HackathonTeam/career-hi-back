package com.careerhi.collector.service;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class SeleniumService {

    private WebDriver createDriver() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-popup-blocking");
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");
        return new ChromeDriver(options);
    }

    public List<String> scrapJobUrls(String searchUrl) {
        WebDriver driver = createDriver();
        Set<String> uniqueUrls = new HashSet<>();
        try {
            driver.get(searchUrl);
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(3));

            List<WebElement> links = new ArrayList<>();
            links.addAll(driver.findElements(By.cssSelector("a.title")));
            links.addAll(driver.findElements(By.cssSelector("a[href*='Detail']")));
            links.addAll(driver.findElements(By.cssSelector("td > a")));

            for (WebElement link : links) {
                String href = link.getAttribute("href");
                if (href != null && href.startsWith("http")) uniqueUrls.add(href);
            }
        } catch (Exception e) {
            System.out.println("❌ URL 수집 에러: " + e.getMessage());
        } finally {
            driver.quit();
        }
        return new ArrayList<>(uniqueUrls);
    }

    public String findImageUrl(String targetUrl) {
        WebDriver driver = createDriver();
        try {
            driver.get(targetUrl);
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(3));

            // 1. 메인 페이지에서 찾기
            String imgSrc = scanImages(driver);
            if (imgSrc != null) return imgSrc;

            // 2. iframe 내부 정밀 탐색 (워크넷, 잡코리아 대응)
            List<WebElement> iframes = driver.findElements(By.tagName("iframe"));
            for (WebElement frame : iframes) {
                try {
                    // 크기가 너무 작은(광고용) iframe은 패스
                    if (frame.getSize().getWidth() < 200 || frame.getSize().getHeight() < 200) continue;

                    driver.switchTo().frame(frame);
                    imgSrc = scanImages(driver);
                    if (imgSrc != null) return imgSrc; // 찾으면 바로 반환
                    driver.switchTo().defaultContent();
                } catch (Exception e) {
                    driver.switchTo().defaultContent();
                }
            }
            return null;
        } catch (Exception e) { return null; }
        finally { driver.quit(); }
    }

    private String scanImages(WebDriver driver) {
        String[] selectors = {"div.cont img", "div.emp_detail img", "div#content-area img", "div.img_area img", "img"};

        for (String selector : selectors) {
            List<WebElement> images = driver.findElements(By.cssSelector(selector));
            for (WebElement img : images) {
                String src = img.getAttribute("src");
                if (src == null || src.contains("icon") || src.contains("logo") || src.contains("btn") || src.contains("button")) continue;

                if (img.getSize().getWidth() > 300 && img.getSize().getHeight() > 100) {
                    return src;
                }
            }
        }
        return null;
    }

    public String getPageText(String targetUrl) {
        WebDriver driver = createDriver();
        StringBuilder sb = new StringBuilder();
        try {
            driver.get(targetUrl);
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(2));

            String[] selectors = {
                    "div.artTplInner", "div.dty_cont", "div.emp_detail", "div.cont",
                    "div.rec_view", "div#content-area", "table", "div.careers-area"
            };

            sb.append(scanText(driver, selectors));

            List<WebElement> iframes = driver.findElements(By.tagName("iframe"));
            for (WebElement frame : iframes) {
                try {
                    if (frame.getSize().getWidth() < 200) continue; // 너무 작은 건 패스
                    driver.switchTo().frame(frame);
                    String iframeText = scanText(driver, selectors);
                    if (!iframeText.isBlank()) {
                        sb.append("\n[iframe 내용]\n").append(iframeText);
                    }
                    driver.switchTo().defaultContent();
                } catch (Exception e) { driver.switchTo().defaultContent(); }
            }

            if (sb.length() < 50) {
                sb.append(driver.findElement(By.tagName("body")).getText());
            }

        } catch (Exception e) {
            System.out.println("❌ 텍스트 수집 에러: " + e.getMessage());
        } finally {
            driver.quit();
        }
        return sb.toString();
    }

    private String scanText(WebDriver driver, String[] selectors) {
        StringBuilder text = new StringBuilder();
        for (String sel : selectors) {
            try {
                List<WebElement> els = driver.findElements(By.cssSelector(sel));
                for (WebElement el : els) text.append(el.getText()).append("\n");
            } catch (Exception ignored) {}
        }
        return text.toString();
    }
}