
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.*;

import java.util.*;

public class WebstaurantTest {
    static WebDriver driver;
    static WebElement nextPageButton;
    static String item;

    static {
        // DRIVER SETUP'S AND NAVIGATE TO APPLICATIONS URL
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get("https://www.webstaurantstore.com/");

        item = "stainless work table";
    }

    public static void main(String[] args) {
        // ENTER THE ITEM AND SEARCH
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

        // END OF THE AUTOMATION CLOSE THE BROWSER SESSION
        driver.close();
    }


    /**
     * GETS ALL THE ITEM IN THE RESULT AND ADD INTO LIST
     * VERIFIES IS EVERY ITEM CONTAINS "Table" OR NOT
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
     * SAVE'S INTO LIST EVERY "Add To Cart" BUTTON
     * CLICK TO THE LAST ITEM
     */
    public static void addToCart() {
        List<WebElement> addToCart = driver.findElements(By.xpath("//input[starts-with(@name,\"addToCartButton\")]"));
        addToCart.get(addToCart.size() - 1).click();
    }

    /**
     * EXPLICIT WAIT FOR CLICK TO ELEMENT
     *
     * @param webElement
     */
    public static void clickTo(WebElement webElement) {
        WebDriverWait wait = new WebDriverWait(driver, 20);
        wait.until(ExpectedConditions.elementToBeClickable(webElement)).click();
    }

    /**
     * CLICK TO CART BUTTON ON THE POP-UP AFTER WE CLICK ADD TO CART
     * CLICK TO EMPTY CART AFTER WE NAVIGATE TO CART PAGE
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
     * SCROLLS ALL THE WAY DOWN FOR GET FULL LIST OF ITEMS
     */
    public static void scrollingDown() {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].scrollIntoView();", driver.findElement(By.xpath("//p[.='Related Searches']")));
    }

    /**
     * I USED EXPLICIT HERE BECAUSE SOMETIMES SELENIUM WORKS FASTER THAN APPLICATION
     * AND IT DOESN'T VERIFIES CART SUCCESSFULLY EMPTIED
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
