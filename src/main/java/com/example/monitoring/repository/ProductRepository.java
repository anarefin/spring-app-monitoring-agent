package com.example.monitoring.repository;

import com.example.monitoring.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    List<Product> findByPriceGreaterThan(Double price);
    
    List<Product> findByNameContaining(String name);
    
    @Query("SELECT p FROM Product p WHERE p.price BETWEEN :minPrice AND :maxPrice")
    List<Product> findByPriceRange(@Param("minPrice") Double minPrice, @Param("maxPrice") Double maxPrice);
    
    @Query("SELECT p FROM Product p ORDER BY p.price DESC")
    List<Product> findAllOrderByPriceDesc();
}
