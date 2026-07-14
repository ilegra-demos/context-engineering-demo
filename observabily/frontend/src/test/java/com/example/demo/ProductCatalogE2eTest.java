package com.example.demo;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class ProductCatalogE2eTest {

    private static Playwright playwright;
    private static Browser browser;
    private BrowserContext context;
    private Page page;

    @BeforeAll
    static void launchBrowser() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
    }

    @AfterAll
    static void closeBrowser() {
        if (browser != null) {
            browser.close();
        }
        if (playwright != null) {
            playwright.close();
        }
    }

    @BeforeEach
    void createContextAndPage() {
        context = browser.newContext();
        page = context.newPage();
    }

    @AfterEach
    void closeContext() {
        if (context != null) {
            context.close();
        }
    }

    @Test
    @DisplayName("Verify product catalog UI structure and adding a new product with discount")
    void testProductCatalogFlow() {
        // 1. Navigate to the app home page
        page.navigate("http://localhost:8081/");

        // 2. Verify page title or header
        Locator header = page.locator("[data-testid='header']");
        assertTrue(header.isVisible());
        assertTrue(header.textContent().contains("Observability Control Room"));

        // 3. Register a new product
        String testProductId = String.valueOf(System.currentTimeMillis() % 100000);
        page.fill("[data-testid='input-id']", testProductId);
        page.fill("[data-testid='input-name']", "E2E Test Keyboard " + testProductId);
        page.fill("[data-testid='input-price']", "300.00");
        page.fill("[data-testid='input-quantity']", "5");
        page.fill("[data-testid='input-cost']", "150.00");
        page.fill("[data-testid='input-tags']", "e2e, hardware, test");

        // Submit the form
        page.click("[data-testid='btn-create-product']");

        // 4. Verify that the product is visible in the catalog
        Locator productCard = page.locator("[data-testid='product-card-" + testProductId + "']");
        assertTrue(productCard.isVisible());

        Locator productName = page.locator("[data-testid='product-name-" + testProductId + "']");
        assertEquals("E2E Test Keyboard " + testProductId, productName.textContent().trim());

        Locator productPrice = page.locator("[data-testid='product-price-" + testProductId + "']");
        assertTrue(productPrice.textContent().trim().contains("300"));

        // 5. Apply discount
        page.fill("[data-testid='input-discount-" + testProductId + "']", "10");
        page.click("[data-testid='btn-apply-discount-" + testProductId + "']");

        // Verify the new price after 10% discount (300.00 * 0.9 = 270.00)
        Locator updatedPrice = page.locator("[data-testid='product-price-" + testProductId + "']");
        assertTrue(updatedPrice.textContent().trim().contains("270")); 
    }

    @Test
    @DisplayName("Verify default products are rendered correctly in the catalog")
    void testDefaultProductsRendering() {
        // 1. Navigate to the app home page
        page.navigate("http://localhost:8081/");

        // 2. Verify that the product list container is visible
        Locator productList = page.locator("[data-testid='product-list']");
        assertTrue(productList.isVisible());

        // 3. Verify that the default product #1 (Teclado Mecânico) is present with correct details
        Locator productCard1 = page.locator("[data-testid='product-card-1']");
        assertTrue(productCard1.isVisible());
        assertEquals("Teclado Mecânico", page.locator("[data-testid='product-name-1']").textContent().trim());
        assertTrue(page.locator("[data-testid='product-price-1']").textContent().trim().contains("350"));
        assertEquals("10", page.locator("[data-testid='product-quantity-1']").textContent().trim());

        // 4. Verify that the default product #2 (Mouse Gamer) is present with correct details
        Locator productCard2 = page.locator("[data-testid='product-card-2']");
        assertTrue(productCard2.isVisible());
        assertEquals("Mouse Gamer", page.locator("[data-testid='product-name-2']").textContent().trim());
        assertTrue(page.locator("[data-testid='product-price-2']").textContent().trim().contains("199.9"));
        assertEquals("5", page.locator("[data-testid='product-quantity-2']").textContent().trim());
    }

    @Test
    @DisplayName("Verify error handling when registering a product with a duplicate ID")
    void testDuplicateProductCreationError() {
        // 1. Navigate to the app home page
        page.navigate("http://localhost:8081/");

        // 2. Submit a product using a duplicate ID '1' (which is already registered as a default product)
        page.fill("[data-testid='input-id']", "1");
        page.fill("[data-testid='input-name']", "Duplicate Product");
        page.fill("[data-testid='input-price']", "100.00");
        page.fill("[data-testid='input-quantity']", "10");
        page.fill("[data-testid='input-cost']", "50.00");
        page.fill("[data-testid='input-tags']", "duplicate");

        // Submit the form
        page.click("[data-testid='btn-create-product']");

        // 3. Verify that an action-error alert is displayed
        Locator errorAlert = page.locator("[data-testid='action-error']");
        assertTrue(errorAlert.isVisible());
        assertTrue(errorAlert.textContent().contains("409 Conflict"));
        assertTrue(errorAlert.textContent().contains("Produto já existe"));
    }
}
