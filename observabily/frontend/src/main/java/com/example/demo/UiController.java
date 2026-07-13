package com.example.demo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;
import org.springframework.http.MediaType;
import org.springframework.core.ParameterizedTypeReference;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Controller
public class UiController {

    private final RestClient restClient;

    public UiController(@Value("${backend.api.url}") String backendUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(backendUrl)
                .build();
    }

    @GetMapping("/")
    public String index(Model model, @RequestParam(value = "error", required = false) String error) {
        try {
            List<Map<String, Object>> products = restClient.get()
                    .uri("/products")
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<Map<String, Object>>>() {});
            model.addAttribute("products", products);
        } catch (Exception e) {
            model.addAttribute("products", Collections.emptyList());
            model.addAttribute("error", "Erro ao conectar com o backend: " + e.getMessage());
        }
        if (error != null) {
            model.addAttribute("actionError", error);
        }
        return "index";
    }

    @PostMapping("/products")
    public String createProduct(@RequestParam String id,
                                @RequestParam String name,
                                @RequestParam double price,
                                @RequestParam int quantity,
                                @RequestParam String cost,
                                @RequestParam String tags) {
        try {
            List<String> tagsList = Arrays.stream(tags.split(","))
                    .map(String::trim)
                    .filter(t -> !t.isEmpty())
                    .toList();

            Map<String, Object> product = new HashMap<>();
            product.put("id", id);
            product.put("name", name);
            product.put("price", price);
            product.put("quantity", quantity);
            product.put("cost", cost);
            product.put("tags", tagsList);

            restClient.post()
                    .uri("/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(product)
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            String encodedError = URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
            return "redirect:/?error=" + encodedError;
        }
        return "redirect:/";
    }

    @PostMapping("/products/{id}/apply-discount")
    public String applyDiscount(@PathVariable String id,
                                @RequestParam("discount_percent") double discountPercent) {
        try {
            Map<String, Object> request = Map.of("discount_percent", discountPercent);
            restClient.post()
                    .uri("/products/{id}/apply-discount", id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            String encodedError = URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
            return "redirect:/?error=" + encodedError;
        }
        return "redirect:/";
    }
}
