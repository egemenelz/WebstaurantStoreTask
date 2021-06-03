
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.*;

import java.util.*;

public class WebstaurantTest {
    static WebDriver driver;
    static WebElement nextPageButton;
    static String item;

    /**
     * OPERATING SYSTEM CONFIGURATION
     * WEB DRIVER SETUP
     * THEN NAVIGATE TO APPLICATION' URL
     */

    static {

        String OS = System.getProperty("os.name");
        System.out.println(OS);

        if (OS.equals("Windows 10")) {
            WebDriverManager.chromedriver().setup();
            driver = new ChromeDriver();
            driver.manage().window().maximize();
            driver.get("https://www.webstaurantstore.com/");

        } else if (OS.equals("Mac OS X")) {
            WebDriverManager.chromedriver().setup();
            driver = new ChromeDriver();
            driver.manage().window().maximize();
            driver.get("https://www.webstaurantstore.com/");
        }else{
            System.exit(1);
        }

        item = "stainless work table";
    }

    public static void main(String[] args) {
        // ENTER THE ITEM IN SEARCH ,AND THEN HIT ENTER
        WebElement searchButton = driver.findElement(By.id("searchval"));
        searchButton.sendKeys(item, Keys.ENTER);

        for (int i = 0; i < 8; i++) {
            scrollingDown();
            allTextOfItems();

            try {
                nextPageButton = driver.findElement(By.cssSelector("li.rc-pagination-next"));
                clickTo(nextPageButton);
            } catch (StaleElementReferenceException se) {
                for (int j = 0; j < 1; j++) {
                    nextPageButton = driver.findElement(By.cssSelector("li.rc-pagination-next"));
                    clickTo(nextPageButton);
                }
            }
        }

        scrollingDown();
        addToCart();
        getEmptyCart();
        verifyCartEmpty();

        /**
         * END OF THE AUTOMATION CLOSES THE BROWSER SESSION
         */
        driver.close();
    }


    /**
     * GETS ALL THE ITEM IN THE RESULT AND ADD INTO LIST
     * VERIFIES IF EVERY ITEM CONTAINS "Table" OR NOT
     */
    public static void allTextOfItems() {
        try {
            List<WebElement> descriptions = driver.findElements(By.xpath("//div[@id='product_listing']//a[contains(@class, \"description\")]"));
            for (WebElement description : descriptions) {
                if (!description.getText().contains("Table")) {
                    System.err.println(description.getText() + " Doesn't Contains Table!!");
                }
            }
        } catch (StaleElementReferenceException e) {
            for (int i = 0; i < 1; i++) {
                List<WebElement> descriptions = driver.findElements(By.xpath("//div[@id='product_listing']//a[contains(@class, \"description\")]"));
                for (WebElement description : descriptions) {
                    if (!description.getText().contains("Table")) {
                        System.err.println(description.getText() + " Doesn't Contains Table!!");
                    }
                }
            }
        }

    }

    /**
     * SAVES EVERY "Add To Cart" BUTTON INTO LIST OF WEBELEMENT
     * AND THEN, CLICKS THE LAST BUTTON
     */
    public static void addToCart() {
        List<WebElement> addToCart = driver.findElements(By.xpath("//input[starts-with(@name,\"addToCartButton\")]"));
        addToCart.get(addToCart.size() - 1).click();
    }

    /**
     * EXPLICIT WAIT FOR CLICK TO WEBELEMENT SAFELY
     * @param webElement
     */
    public static void clickTo(WebElement webElement) {
        WebDriverWait wait = new WebDriverWait(driver, 20);
        wait.until(ExpectedConditions.elementToBeClickable(webElement)).click();
    }

    /**
     * CLICKS CART BUTTON ON THE POP-UP AFTER WE CLICK "Add to Cart" BUTTON
     * AFTER THAT CLICKS "Empty Cart" BUTTON AFTER WE NAVIGATE TO "Cart" PAGE
     * AND ACCEPTS EMPTY CART ALERT
     */
    public static void getEmptyCart() {
        WebElement cart = driver.findElement(By.cssSelector("a[class='btn btn-small btn-primary']"));
        clickTo(cart);
        WebElement emptyCartButton = driver.findElement(By.xpath("//a[.='Empty Cart']"));
        clickTo(emptyCartButton);
        WebElement emptyCartAccept = driver.findElement(By.xpath("//button[.=\"Empty Cart\"]"));
        clickTo(emptyCartAccept);
    }

    /**
     * SCROLLS ALL THE WAY DOWN TO GET FULL LIST OF ITEMS
     */
    public static void scrollingDown() {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].scrollIntoView();", driver.findElement(By.xpath("//p[.='Related Searches']")));
    }

    /**
     * I USED EXPLICIT HERE BECAUSE SOMETIMES SELENIUM WORKS FASTER THAN APPLICATION
     * AND IT DOESN'T VERIFIES IF CART IS SUCCESSFULLY EMPTIED
     */
    public static void verifyCartEmpty() {
        WebDriverWait wait = new WebDriverWait(driver, 10);
        String expectedMessage = wait.until(ExpectedConditions.visibilityOf(driver.findElement(By.cssSelector("p[class='header-1']")))).getText();
        String actualMessage = "Your cart is empty.";

        if (!expectedMessage.equals(actualMessage)) {
            System.err.println("Cart Isn't Empty!!");
        }
    }

}
