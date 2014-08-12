import net.jsourcerer.webdriver.jserrorcollector.JavaScriptError;
import org.junit.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: vanle
 * Date: 11.08.14
 * Time: 23:54
 * To change this template use File | Settings | File Templates.
 */
public class Test1 {

    private WebDriver webDriver;

    @Test
    public void test() throws IOException {
        FirefoxProfile ffProfile = new FirefoxProfile();
        JavaScriptError.addExtension(ffProfile);
        String linksString = new String(Files.readAllBytes(Paths.get("D:\\Workspace\\untitled1\\src\\resources\\dev2.csv")));
        webDriver = new FirefoxDriver(ffProfile);
        webDriver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
        //getRelativePathToImageWithClass(Test1.class.getResource("/"), "resources/dev2.csv");
        String[] links = linksString.split("\\n");
        int u = 1;
        System.out.println("### start displaying errors...");
        for(String link : links){
            webDriver.get(link);
            waitForPageLoaded(webDriver);
            if(link.contains("/login") && !isElementPresent(By.xpath("//a[@title='Log Out']"))){
                webDriver.findElement(By.id("email")).clear();
                webDriver.findElement(By.id("email")).sendKeys("vanle@ukr.net");
                webDriver.findElement(By.id("pass")).clear();
                webDriver.findElement(By.id("pass")).sendKeys("090289");
                webDriver.findElement(By.id("send2")).click();
                waitForElementDisplayed(By.xpath("//*[contains(text(),'My Dashboard')]"), 240);
                link = webDriver.getCurrentUrl();
            }
            if(isElementPresent(By.xpath("//button[@title='Add to Cart']"))){
                webDriver.findElement(By.xpath("//button[@title='Add to Cart']")).click();
            }
            if(isElementPresent(By.xpath("//img[@alt='Proceed to Checkout']"))){
                webDriver.findElement(By.xpath("//img[@alt='Proceed to Checkout']")).click();
                waitForElementDisplayed(By.id("place-order-button"), 240);
                link = webDriver.getCurrentUrl();
            }
            System.out.println("--------------------------------------------------------------------------------");
            System.out.println(u + ". URL: " + link);

            List<WebElement> allImages = webDriver.findElements(By.tagName("img"));
            List<String> listErr = new ArrayList<String>();
            for (WebElement image : allImages) {
                String url = image.getAttribute("src");
                if(url == null){
                    break;
                }
                URL obj = new URL(url);
                try{
                    HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                    con.setRequestMethod("GET");
                    int code = con.getResponseCode();
                    if(code == 404 || code != 200){
                        String er = "  Error " + code + " for GET " + url;
                        listErr.add(er);
                    }
                }
                catch(Exception e){

                }
               // con.setRequestProperty("User-Agent", USER_AGENT);
               // int responseCode = con.getResponseCode();
            }
            Set set = new TreeSet(listErr);
            List<String> newListErr = new ArrayList(set);
            for(String er: newListErr){
                System.out.println(er);
            }

            System.out.println("  ");

            List<JavaScriptError> jsErrors = JavaScriptError.readErrors(webDriver);

            if(jsErrors.isEmpty()){
                System.out.println("  Errors not detected.  ");
            }
            else{
                List<String> errList = new ArrayList<String>();
                List<String> errListFaceBook = new ArrayList<String>();
                for(int i = 0; i < jsErrors.size(); i++) {
                    /*map = new HashMap();
                    map.put("error", "");
                    map.put("line", "");
                    map.put("source", "");*/
                    if(jsErrors.get(i).getSourceName().contains("facebook")) {
                        errListFaceBook.add(jsErrors.get(i).getErrorMessage() + "\n" + jsErrors.get(i).getLineNumber() + "\n" + jsErrors.get(i).getSourceName());
                    }
                    else{
                        errList.add(jsErrors.get(i).getErrorMessage() + "\n" + jsErrors.get(i).getLineNumber() + "\n" + jsErrors.get(i).getSourceName());
                    }
                }
                Set set2 = new TreeSet(errList);
                List<String> newListErr2 = new ArrayList(set2);

                Set set3 = new TreeSet(errListFaceBook);
                List<String> newListErr3 = new ArrayList(set3);

                // print unique errors
                for(String str : newListErr2){
                    String[] st = str.split("\\n");
                    System.out.println("  1. Error: " + st[0]);
                    System.out.println("  2. Line Number: " + st[1]);
                    System.out.println("  3. Source Name: " + st[2]);
                    System.out.println("  ");
                }
                for(String str : newListErr3){
                    String[] st = str.split("\\n");
                    System.out.println("  1. Error: " + st[0]);
                    System.out.println("  2. Line Number: " + st[1]);
                    System.out.println("  3. Source Name: " + st[2]);
                    System.out.println("  ");
                    break;
                }
            }
            u++;

        }
        System.out.println("### finish displaying errors.");
        webDriver.close();
        webDriver.quit();
    }


    public static String getRelativePathToImageWithClass(final URL classURL, final String fileName) {
        if (fileName.equals("")) {
            return "";
        }
        String uploadFileName = classURL.getFile();
        int indexOfClasses = uploadFileName.lastIndexOf("target");
        uploadFileName = uploadFileName.substring(1, indexOfClasses);
        uploadFileName = uploadFileName.concat(fileName);
        uploadFileName = uploadFileName.replace("/", "\\");
        return uploadFileName;
    }

    public boolean isElementPresent(By element) {
        try {
            webDriver.findElement(element);
            return true;
        } catch (org.openqa.selenium.NoSuchElementException e) {
            return false;
        }
    }

    public void waitForElementDisplayed(final By locator, int sec) {
        WebDriverWait wait = new WebDriverWait(webDriver, sec);
        wait.until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver webDriver) {
                return webDriver.findElement(locator).isDisplayed();
            }
        });
    }


    public void waitForPageLoaded(WebDriver driver) {
        ExpectedCondition<Boolean> expectation = new
                ExpectedCondition<Boolean>() {
                    public Boolean apply(WebDriver driver) {
                        return ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete");
                    }
                };

        Wait<WebDriver> wait = new WebDriverWait(driver, Long.parseLong(String.valueOf(30000 / 1000)));
        try {
            wait.until(expectation);
        } catch (Throwable error) {
            Assert.fail("Timeout waiting for Page Load Request to complete.", error);
        }
    }

}
