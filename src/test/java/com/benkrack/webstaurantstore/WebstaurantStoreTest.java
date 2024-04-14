package com.benkrack.webstaurantstore;

import io.github.bonigarcia.wdm.WebDriverManager;
import java.time.Duration;
import java.util.List;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.Keys;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * WebDriver tests for the WebstaurantStore website.
 * 
 * The purpose of the tests in this class is to simulate and validate certain user interactions with the website.
 * Validations are included for search, product title, and cart functionality.
 * 
 * @author Ben Krack
 */
public class WebstaurantStoreTest {

    private WebDriver driver;
    private WebDriverWait wait;

    private static final Duration TIMEOUT = Duration.ofSeconds(5);

    private static final String BASE_URL = "https://www.webstaurantstore.com/";
    private static final String CART_PATH = "cart";
    private static final String EMPTY_CART_TEXT = "Your cart is empty.";
    private static final String KEYWORD = "Table";
    private static final String PAGE_QUERY_PARAMETER = "page";
    private static final String QUERY = "stainless steel table";

    private static final String ITEM_ADD_CONFIRM_ID = "watnotif-wrapper";
    private static final String SEARCH_INPUT_ID = "searchval";

    private static final String DELETE_ITEM_XPATH = "//button[contains(@class, 'deleteCartItemButton')]";
    private static final String LAST_PAGE_BREADCRUMB_LINK_XPATH = "//a[contains(@aria-label, 'last page')]";

    private static final String ITEM_ADD_CART_CSS_SELECTOR = "[data-testid=itemAddCart]";
    private static final String ITEM_DESCRIPTION_CSS_SELECTOR = "[data-testid='itemDescription']";
    private static final String PRODUCT_BOX_CONTAINER_CSS_SELECTOR = "[data-testid=productBoxContainer]";

    private static final String EMPTY_CART_BOX_CLASS_NAME = "empty-cart__inner";
    private static final String EMPTY_CART_TEXT_CLASS_NAME = "header-1";


    /**
     * Sets up WebDriver and WebDriverWait and navigates to the WebstaurantStore home page at {@link #BASE_URL}.
     */
    @BeforeEach
    public void setup() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, TIMEOUT);
        driver.get(BASE_URL);
    }

    /**
     * Tests the following features of the WebstaurantStore website:
     * 2. Search with a specific query: {@link #QUERY}.
     * 2. Check that each product on the first search results page has the word {@link #KEYWORD} in its title.
     * 3. Add the last of found items to Cart.
     * 4. Empty Cart.
     * 
     * @throws NumberFormatException if text value of breadcrumb is not a number.
     */
    @Test
    public void testFirstPageSearchResultsContainKeywordAndVerifyCartLogic() throws Exception {
        // Search a specific query.
        WebElement searchInput = driver.findElement(By.id(SEARCH_INPUT_ID));
        searchInput.sendKeys(QUERY);
        searchInput.sendKeys(Keys.ENTER);

        // Wait for search page to load and validate the page.
        waitForSearchResults();
        validateCurrentPage();

        // Test cart logic.
        addLastItemToCart();
        emptyCart();
    }

    /**
     * Tests the following features of the WebstaurantStore website:
     * 2. Search with a specific query: {@link #QUERY}.
     * 2. Check the search result ensuring every product has the word {@link #KEYWORD} in its title.
     * 3. Add the last of found items to Cart.
     * 4. Empty Cart.
     * 
     * @throws NumberFormatException if text value of breadcrumb is not a number.
     */
    @Disabled
    @Test
    public void testAllResultsContainsTableAndCartLogic() throws Exception {
        // Search a specific query.
        WebElement searchInput = driver.findElement(By.id(SEARCH_INPUT_ID));
        searchInput.sendKeys(QUERY);
        searchInput.sendKeys(Keys.ENTER);

        // Wait for search page to load.
        waitForSearchResults();

        // Get the total number of pages of results.
        int numResultsPages = calcNumberOfResultsPages();
        
        // Validate the rest of the pages of results.
        String baseSearchUrl = driver.getCurrentUrl();
        for (int i = 1; i <= numResultsPages; i++) {
            navigateToResultsPage(baseSearchUrl, i);
            validateCurrentPage();
        }

        // Test cart logic.
        addLastItemToCart();
        emptyCart();
    }

    /**
     * Validates that all product descriptions on the current page contain the keyword {@link #KEYWORD}.
     */
    private void validateCurrentPage() {
        List<WebElement> resultsContainers = driver.findElements(By.cssSelector(PRODUCT_BOX_CONTAINER_CSS_SELECTOR));
        for (WebElement container : resultsContainers) {
            WebElement description = container.findElement(By.cssSelector(ITEM_DESCRIPTION_CSS_SELECTOR));
            Assertions.assertTrue(description.getText().contains(KEYWORD), "Item with description '" + 
            description.getText() + "' does not contain the keyword " + KEYWORD);
        }
    }

    /**
     * Navigates to the results page with page number {@code pageNumber}.
     * @param pageNumber the number of the results page to navigate to.
     */
    private void navigateToResultsPage(String baseSearchUrl, int pageNumber) {
        // Construct the URL to the results page for the specifice page number.
        String resultsPageUrl = baseSearchUrl + "?" + PAGE_QUERY_PARAMETER + "=" + pageNumber;
        driver.get(resultsPageUrl);

        waitForSearchResults();
    }

    /**
     * Performs the following:
     * 1. Navigates to the WebstaurantStore cart page.
     * 2. Empties the cart.
     * 3. Asserts that the cart is empty.
     */
    private void emptyCart() throws Exception {
        driver.get(BASE_URL + CART_PATH);
        WebElement deleteItem = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.xpath(DELETE_ITEM_XPATH)));
        deleteItem.click();
        wait.until(ExpectedConditions.presenceOfElementLocated(By.className(EMPTY_CART_BOX_CLASS_NAME)));
        String cartEmptyText = driver.findElement(By.className(EMPTY_CART_TEXT_CLASS_NAME)).getText();
        Assertions.assertEquals(cartEmptyText, EMPTY_CART_TEXT);
    }

    /**
     * Gets the list of products on the page and adds the last one to the cart, waiting for the operation to complete.
     */
    private void addLastItemToCart() {
        List<WebElement> resultsContainers = driver.findElements(By.cssSelector(PRODUCT_BOX_CONTAINER_CSS_SELECTOR));
        WebElement submitButton = resultsContainers.get(resultsContainers.size() - 1)
            .findElement(By.cssSelector(ITEM_ADD_CART_CSS_SELECTOR));
        submitButton.click();
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id(ITEM_ADD_CONFIRM_ID)));
    }

    /**
     * Waits until the first search result loads.
     */
    private void waitForSearchResults() {
        wait.until(ExpectedConditions.visibilityOf(
            driver.findElement(By.cssSelector(PRODUCT_BOX_CONTAINER_CSS_SELECTOR))));
    }

    /**
     * Calculates the number of pages returned by a search by parsing the last breadcrumb value.
     *
     * @return the number of results pages returned by a search.
     * @throws NumberFormatException if text value of breadcrumb is not a number.
     */
    private int calcNumberOfResultsPages() throws NumberFormatException {
        WebElement lastPageLink = null;
        try {
            lastPageLink = driver.findElement(By.xpath(LAST_PAGE_BREADCRUMB_LINK_XPATH));
        } catch (NoSuchElementException e) {
            // TODO(benkrack): Implement logging and log exception.
        }

        // If there is only one page, return 1. Else return the value of the last breadcrumb link.
        return lastPageLink == null ? 1 : Integer.valueOf(lastPageLink.getText());
    }

    /**
     * Tears down the WebDriver instance.
     */
    @AfterEach
    public void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
