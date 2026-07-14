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
    @DisplayName("Verify product list display on initial page load")
    void testProductListDisplay() {
        // 1. Navigate to the app home page
        page.navigate("http://localhost:8081/");

        // 2. Verify main sections are visible
        Locator header = page.locator("[data-testid='header']");
        assertTrue(header.isVisible());
        assertTrue(header.textContent().contains("Observability Control Room"));

        Locator createForm = page.locator("[data-testid='create-product-form']");
        assertTrue(createForm.isVisible());

        Locator productList = page.locator("[data-testid='product-list']");
        assertTrue(productList.isVisible());

        // 3. Verify standard pre-seeded products (ID #1 and ID #2) are visible
        Locator product1Card = page.locator("[data-testid='product-card-1']");
        assertTrue(product1Card.isVisible());
        Locator product1Name = page.locator("[data-testid='product-name-1']");
        assertEquals("Teclado Mecânico", product1Name.textContent().trim());
        Locator product1Price = page.locator("[data-testid='product-price-1']");
        assertTrue(product1Price.textContent().contains("350"));

        Locator product2Card = page.locator("[data-testid='product-card-2']");
        assertTrue(product2Card.isVisible());
        Locator product2Name = page.locator("[data-testid='product-name-2']");
        assertEquals("Mouse Gamer", product2Name.textContent().trim());
        Locator product2Price = page.locator("[data-testid='product-price-2']");
        assertTrue(product2Price.textContent().contains("199.9"));
    }
}

