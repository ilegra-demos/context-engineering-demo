package com.example.demo;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Counter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@RestController
public class ProductController {

    private final Map<String, Map<String, Object>> productsDb = new ConcurrentHashMap<>();
    private final Counter productsCreatedCounter;
    private final Counter discountsAppliedCounter;
    private final Counter discountErrorsCounter;

    public ProductController(MeterRegistry meterRegistry) {
        // Initialize products with some sample data
        Map<String, Object> p1 = new HashMap<>();
        p1.put("id", "1");
        p1.put("name", "Teclado Mecânico");
        p1.put("price", 350.0);
        p1.put("quantity", 10);
        p1.put("cost", "150.50");
        p1.put("tags", Arrays.asList("hardware", "periféricos"));
        productsDb.put("1", p1);

        Map<String, Object> p2 = new HashMap<>();
        p2.put("id", "2");
        p2.put("name", "Mouse Gamer");
        p2.put("price", 199.9);
        p2.put("quantity", 5);
        p2.put("cost", "80.00");
        p2.put("tags", Arrays.asList("hardware", "periféricos"));
        productsDb.put("2", p2);

        // Define custom Micrometer metrics
        this.productsCreatedCounter = Counter.builder("products.created")
                .description("Number of products created")
                .register(meterRegistry);

        this.discountsAppliedCounter = Counter.builder("discounts.applied")
                .description("Number of successful discounts applied")
                .register(meterRegistry);

        this.discountErrorsCounter = Counter.builder("discounts.errors")
                .description("Number of failures during discount application")
                .register(meterRegistry);
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> status = new HashMap<>();
        status.put("status", "ok");
        return ResponseEntity.ok(status);
    }

    @PostMapping("/products")
    public ResponseEntity<?> createProduct(@RequestBody Map<String, Object> product) {
        String id = (String) product.get("id");
        if (id == null || !product.containsKey("name") || !product.containsKey("price")) {
            return ResponseEntity.badRequest().body(Map.of("error", "Campos obrigatórios: id, name, price"));
        }
        if (productsDb.containsKey(id)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", "Produto já existe"));
        }
        productsDb.put(id, product);
        productsCreatedCounter.increment();
        return ResponseEntity.status(HttpStatus.CREATED).body(product);
    }

    @GetMapping("/products")
    public Collection<Map<String, Object>> listProducts() {
        return productsDb.values();
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<?> getProduct(@PathVariable String id) {
        Map<String, Object> product = productsDb.get(id);
        if (product == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(product);
    }

    @PostMapping("/products/{id}/apply-discount")
    public ResponseEntity<?> applyDiscount(@PathVariable String id, @RequestBody Map<String, Object> request) {
        Map<String, Object> product = productsDb.get(id);
        if (product == null) {
            return ResponseEntity.notFound().build();
        }

        Object discountRaw = request.get("discount_percent");
        if (discountRaw == null) {
            discountErrorsCounter.increment();
            return ResponseEntity.badRequest().body(Map.of("error", "discount_percent é obrigatório"));
        }

        double discountPercent;
        try {
            discountPercent = Double.parseDouble(discountRaw.toString());
        } catch (NumberFormatException e) {
            discountErrorsCounter.increment();
            return ResponseEntity.badRequest().body(Map.of("error", "discount_percent inválido"));
        }

        if (discountPercent < 0 || discountPercent > 90) {
            discountErrorsCounter.increment();
            return ResponseEntity.badRequest().body(Map.of("error", "Desconto acima do limite permitido (90%) ou negativo"));
        }

        double price = Double.parseDouble(product.get("price").toString());
        double newPrice = Math.round(price * (1 - discountPercent / 100) * 100.0) / 100.0;
        product.put("price", newPrice);
        productsDb.put(id, product);

        discountsAppliedCounter.increment();
        return ResponseEntity.ok(product);
    }
}
