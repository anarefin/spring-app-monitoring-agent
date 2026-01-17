package com.example.monitoring.controller;

import com.example.monitoring.model.Product;
import com.example.monitoring.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Random;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Slf4j
public class ProductController {

    private final ProductRepository productRepository;
    private final Random random = new Random();

    @GetMapping
    public List<Product> getAllProducts() throws InterruptedException {
        // Simulate random latency
        simulateLatency();
        return productRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) throws InterruptedException {
        simulateLatency();
        return productRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Product createProduct(@RequestBody Product product) throws InterruptedException {
        simulateLatency();
        return productRepository.save(product);
    }

    private void simulateLatency() throws InterruptedException {
        // 10% chance of high latency (500ms - 1000ms)
        // 90% chance of low latency (10ms - 50ms)
        if (random.nextDouble() < 0.1) {
            long delay = 500 + random.nextInt(500);
            log.info("Simulating high latency: {}ms", delay);
            Thread.sleep(delay);
        } else {
            long delay = 10 + random.nextInt(40);
            Thread.sleep(delay);
        }

        // 1% chance of error
        if (random.nextDouble() < 0.01) {
            throw new RuntimeException("Simulated random error");
        }
    }
}
