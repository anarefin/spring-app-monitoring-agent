package com.example.monitoring;

import com.example.monitoring.model.Product;
import com.example.monitoring.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.stream.IntStream;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final ProductRepository productRepository;

    @Override
    public void run(String... args) throws Exception {
        if (productRepository.count() == 0) {
            log.info("Seeding data...");
            IntStream.rangeClosed(1, 50).forEach(i -> {
                Product product = new Product();
                product.setName("Product " + i);
                product.setPrice(10.0 * i);
                product.setDescription("Description for product " + i);
                productRepository.save(product);
            });
            log.info("Data seeding completed.");
        }
    }
}
