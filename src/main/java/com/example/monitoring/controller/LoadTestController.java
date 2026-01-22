package com.example.monitoring.controller;

import com.example.monitoring.model.Product;
import com.example.monitoring.repository.ProductRepository;
import com.example.monitoring.service.LoadTestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RestController
@RequestMapping("/api/load-test")
@RequiredArgsConstructor
@Slf4j
public class LoadTestController {

    private final ProductRepository productRepository;
    private final LoadTestService loadTestService;
    private final ExecutorService executorService = Executors.newFixedThreadPool(50);

    /**
     * Light load: Sequential queries
     * Use this to see baseline connection usage
     */
    @GetMapping("/light")
    public ResponseEntity<Map<String, Object>> lightLoad() {
        log.info("Starting light load test - Sequential queries");
        long startTime = System.currentTimeMillis();
        
        List<Product> products = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            products.addAll(productRepository.findAll());
        }
        
        long duration = System.currentTimeMillis() - startTime;
        
        return ResponseEntity.ok(Map.of(
            "test", "light-load",
            "queries", 10,
            "type", "sequential",
            "duration_ms", duration,
            "results_count", products.size()
        ));
    }

    /**
     * Medium load: Parallel queries (10 concurrent)
     * Use this to see connection pool growing to ~10-15 connections
     */
    @GetMapping("/medium")
    public ResponseEntity<Map<String, Object>> mediumLoad() {
        log.info("Starting medium load test - 10 concurrent queries");
        long startTime = System.currentTimeMillis();
        
        List<CompletableFuture<List<Product>>> futures = IntStream.range(0, 10)
            .mapToObj(i -> CompletableFuture.supplyAsync(() -> 
                loadTestService.executeQueryWithDelay(i, 2.0), executorService))
            .collect(Collectors.toList());
        
        // Wait for all to complete
        List<Product> allProducts = futures.stream()
            .map(CompletableFuture::join)
            .flatMap(List::stream)
            .collect(Collectors.toList());
        
        long duration = System.currentTimeMillis() - startTime;
        
        return ResponseEntity.ok(Map.of(
            "test", "medium-load",
            "queries", 10,
            "type", "concurrent",
            "duration_ms", duration,
            "results_count", allProducts.size()
        ));
    }

    /**
     * Heavy load: Parallel queries (30 concurrent)
     * Use this to push connection pool to its limits (near maximum-pool-size)
     */
    @GetMapping("/heavy")
    public ResponseEntity<Map<String, Object>> heavyLoad(@RequestParam(defaultValue = "30") int concurrency) {
        log.info("Starting heavy load test - {} concurrent queries", concurrency);
        long startTime = System.currentTimeMillis();
        
        List<CompletableFuture<List<Product>>> futures = IntStream.range(0, concurrency)
            .mapToObj(i -> CompletableFuture.supplyAsync(() -> 
                loadTestService.executeQueryWithDelay(i, 3.0), executorService))
            .collect(Collectors.toList());
        
        // Wait for all to complete
        List<Product> allProducts = futures.stream()
            .map(CompletableFuture::join)
            .flatMap(List::stream)
            .collect(Collectors.toList());
        
        long duration = System.currentTimeMillis() - startTime;
        
        return ResponseEntity.ok(Map.of(
            "test", "heavy-load",
            "queries", concurrency,
            "type", "concurrent",
            "duration_ms", duration,
            "results_count", allProducts.size()
        ));
    }

    /**
     * Connection leak simulation
     * Use this to see what happens when connections are held for a long time
     */
    @GetMapping("/sustained")
    public ResponseEntity<Map<String, Object>> sustainedLoad(
            @RequestParam(defaultValue = "15") int connections,
            @RequestParam(defaultValue = "5000") int holdTime) {
        log.info("Starting sustained load test - {} connections held for {}ms", connections, holdTime);
        long startTime = System.currentTimeMillis();
        
        List<CompletableFuture<String>> futures = IntStream.range(0, connections)
            .mapToObj(i -> CompletableFuture.supplyAsync(() -> {
                log.debug("Connection {} - Starting long running query", i);
                try {
                    // Simulate long-running query
                    List<Product> products = productRepository.findAll();
                    Thread.sleep(holdTime);
                    return "Query " + i + " completed with " + products.size() + " results";
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return "Query " + i + " interrupted";
                }
            }, executorService))
            .collect(Collectors.toList());
        
        // Wait for all to complete
        List<String> results = futures.stream()
            .map(CompletableFuture::join)
            .collect(Collectors.toList());
        
        long duration = System.currentTimeMillis() - startTime;
        
        return ResponseEntity.ok(Map.of(
            "test", "sustained-load",
            "connections", connections,
            "hold_time_ms", holdTime,
            "duration_ms", duration,
            "completed", results.size()
        ));
    }

    /**
     * Stress test: Push pool to maximum and beyond
     * Use this to see connection timeout behavior
     */
    @GetMapping("/stress")
    public ResponseEntity<Map<String, Object>> stressTest() {
        log.info("Starting stress test - 50 concurrent queries to exceed pool size");
        long startTime = System.currentTimeMillis();
        int concurrency = 50;
        
        List<CompletableFuture<Map<String, Object>>> futures = new ArrayList<>();
        for (int i = 0; i < concurrency; i++) {
            final int queryNum = i;
            CompletableFuture<Map<String, Object>> future = CompletableFuture.supplyAsync(() -> {
                long queryStart = System.currentTimeMillis();
                try {
                    List<Product> products = productRepository.findAll();
                    long queryDuration = System.currentTimeMillis() - queryStart;
                    Map<String, Object> result = new HashMap<>();
                    result.put("query", queryNum);
                    result.put("status", "success");
                    result.put("duration_ms", queryDuration);
                    result.put("results", products.size());
                    return result;
                } catch (Exception e) {
                    long queryDuration = System.currentTimeMillis() - queryStart;
                    Map<String, Object> result = new HashMap<>();
                    result.put("query", queryNum);
                    result.put("status", "failed");
                    result.put("duration_ms", queryDuration);
                    result.put("error", e.getMessage());
                    return result;
                }
            }, executorService);
            futures.add(future);
        }
        
        // Wait for all to complete
        List<Map<String, Object>> results = futures.stream()
            .map(CompletableFuture::join)
            .collect(Collectors.toList());
        
        long duration = System.currentTimeMillis() - startTime;
        long successful = results.stream().filter(r -> "success".equals(r.get("status"))).count();
        long failed = results.stream().filter(r -> "failed".equals(r.get("status"))).count();
        
        Map<String, Object> response = new HashMap<>();
        response.put("test", "stress-test");
        response.put("total_queries", concurrency);
        response.put("successful", successful);
        response.put("failed", failed);
        response.put("duration_ms", duration);
        response.put("details", results);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Visual test: Hold connections to easily see in Grafana
     * Use this to clearly visualize connection pool behavior
     */
    @GetMapping("/visual")
    public ResponseEntity<Map<String, Object>> visualTest(
            @RequestParam(defaultValue = "15") int connections,
            @RequestParam(defaultValue = "10") int holdSeconds) {
        log.info("Starting visual test - {} connections held for {} seconds", connections, holdSeconds);
        long startTime = System.currentTimeMillis();
        
        List<CompletableFuture<List<Product>>> futures = IntStream.range(0, connections)
            .mapToObj(i -> CompletableFuture.supplyAsync(() -> {
                log.info("Connection {} - Starting (watch Grafana now!)", i);
                return loadTestService.executeQueryWithDelay(i, (double) holdSeconds);
            }, executorService))
            .collect(Collectors.toList());
        
        log.info("All {} connections are now being acquired - check Grafana!", connections);
        
        // Wait for all to complete
        List<List<Product>> results = futures.stream()
            .map(CompletableFuture::join)
            .collect(Collectors.toList());
        
        long duration = System.currentTimeMillis() - startTime;
        int totalResults = results.stream().mapToInt(List::size).sum();
        
        return ResponseEntity.ok(Map.of(
            "test", "visual-test",
            "connections", connections,
            "hold_seconds", holdSeconds,
            "duration_ms", duration,
            "message", "Check Grafana dashboard - you should see " + connections + " active connections",
            "completed", results.size(),
            "total_products", totalResults
        ));
    }

    /**
     * Get current stats
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        long count = productRepository.count();
        return ResponseEntity.ok(Map.of(
            "message", "Load test endpoints ready",
            "products_in_db", count,
            "endpoints", List.of(
                "/api/load-test/light - Sequential queries (baseline)",
                "/api/load-test/medium - 10 concurrent queries (held 2s)",
                "/api/load-test/heavy?concurrency=30 - Heavy concurrent load (held 3s)",
                "/api/load-test/visual?connections=15&holdSeconds=10 - Visual test for Grafana",
                "/api/load-test/sustained?connections=15&holdTime=5000 - Long-running queries",
                "/api/load-test/stress - Stress test (50 queries)"
            )
        ));
    }
}
