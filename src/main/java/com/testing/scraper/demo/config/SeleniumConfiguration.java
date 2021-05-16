package com.testing.scraper.demo.config;

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

@Configuration
public class SeleniumConfiguration {

    private final String chromeDriverPath = "./src/main/resources/ChromeDriver90.0.4430.24.exe";

    @PostConstruct
    public void postConstruct() {
        System.setProperty("webdriver.chrome.driver",chromeDriverPath);
    }

    @Bean
    public ChromeDriver driver() {
        ChromeDriver driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
        return driver;
    }
}
