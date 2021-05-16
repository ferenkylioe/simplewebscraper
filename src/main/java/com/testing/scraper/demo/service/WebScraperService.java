package com.testing.scraper.demo.service;

import com.opencsv.CSVWriter;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import com.testing.scraper.demo.object.Product;
import lombok.AllArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@AllArgsConstructor
public class WebScraperService {

    private final String URL = "https://www.tokopedia.com/p/handphone-tablet";
    private ChromeDriver driver;

    @PostConstruct
    void postConstruct() {
        execute(100);
    }

    public void execute(int maxProduct) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        driver.get(URL);

        List<WebElement> listProduct;
        while(true) {
            //scroll to down, each scroll add height 500
            js.executeScript("window.scrollBy(0,500)");

            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            WebElement results = new WebDriverWait(driver, 10)
                    .until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@data-testid='divComp#62362']")));
            listProduct = results.findElements(By.xpath(".//div[@data-testid='master-product-card']"));

            if( listProduct.size() >= maxProduct ) {
                break;
            }
        }

        ArrayList<String> tabs;
        String urlProductDetail;
        List<Product> resultProduct = new ArrayList<>();
        Product product;
        int count = 1;
        for(WebElement webElement : listProduct) {
            //find URL product detail
            urlProductDetail = webElement.findElement(By.tagName("a")).getAttribute("href");

            //open new tab URL product detail
            driver.executeScript ("window.open('" + urlProductDetail +"');");

            //get window id
            tabs = new ArrayList<String>(driver.getWindowHandles());

            //switch to tab product detail
            driver.switchTo().window(tabs.get(1));

            product = new Product();

            WebElement results = new WebDriverWait(driver, 10)
                    .until(ExpectedConditions.presenceOfElementLocated(By.xpath("//h1[@data-testid='lblPDPDetailProductName']")));
            product.setName(results.getText());

            results = driver.findElement(By.xpath("//div[@data-testid='lblPDPDescriptionProduk']"));
            product.setDescription(results.getText());

            results = driver.findElement(By.xpath("//div[@data-testid='PDPImageMain']"));
            product.setImageLink(results.findElement(By.tagName("img")).getAttribute("src"));

            results = driver.findElement(By.xpath("//div[@data-testid='lblPDPDetailProductPrice']"));
            product.setPrice(results.getText());

            results = driver.findElement(By.xpath("//span[@data-testid='lblPDPDetailProductRatingNumber']"));
            product.setRating(results.getText());

            results = driver.findElement(By.xpath("//a[@data-testid='llbPDPFooterShopName']"));
            product.setStoreName(results.findElement(By.tagName("h2")).getText());

            resultProduct.add(product);

            //close tab product detail
            driver.close();

            //switch to tab product list
            driver.switchTo().window(tabs.get(0));

            if( count >= maxProduct ) {
                break;
            }

            count++;
        }

        driver.quit();

        writeToCSV(resultProduct);
    }

    private void writeToCSV(List<Product> resultProduct) {
        try {
            Writer writer  = new FileWriter("result.csv");

            StatefulBeanToCsv sbc = new StatefulBeanToCsvBuilder(writer)
                    .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
                    .build();

            sbc.write(resultProduct);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CsvRequiredFieldEmptyException e) {
            e.printStackTrace();
        } catch (CsvDataTypeMismatchException e) {
            e.printStackTrace();
        }
    }
}
