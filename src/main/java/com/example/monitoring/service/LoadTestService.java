package com.example.monitoring.service;

import com.example.monitoring.model.Product;
import com.example.monitoring.repository.ProductRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoadTestService {

    private final ProductRepository productRepository;
    
    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Execute a query and hold the database connection using pg_sleep
     * This ensures the connection stays active and visible in HikariCP metrics
     */
    @Transactional
    public List<Product> executeQueryWithDelay(int queryNum, double delaySeconds) {
        log.debug("Query {} - Acquiring connection", queryNum);
        
        // First, get the products
        List<Product> products = productRepository.findAll();
        
        // Then use PostgreSQL's pg_sleep to hold the connection
        // This keeps the database connection genuinely busy
        Query sleepQuery = entityManager.createNativeQuery("SELECT pg_sleep(:seconds)");
        sleepQuery.setParameter("seconds", delaySeconds);
        sleepQuery.getSingleResult();
        
        log.debug("Query {} - Releasing connection after {} seconds", queryNum, delaySeconds);
        return products;
    }
}
