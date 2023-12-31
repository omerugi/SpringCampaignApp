package com.example.mabaya.repositories;

import com.example.mabaya.dto.projections.TopProductProjection;
import com.example.mabaya.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepo extends JpaRepository<Product, String> {

    @Query(value = "SELECT c.bid, p.*, cat.name as category \n" +
            "FROM Product p\n" +
            "JOIN product_campaign pc ON p.product_serial_number = pc.product_serial_number\n" +
            "JOIN Campaign c ON pc.campaign_id = c.id\n" +
            "JOIN category cat ON cat.id = p.category_id\n" +
            "WHERE c.active = TRUE AND p.active = TRUE\n" +
            "ORDER BY CASE WHEN cat.name = :category THEN 0 ELSE 1 END, c.bid DESC\n " +
            "LIMIT 1;", nativeQuery = true)
    Optional<TopProductProjection> findTopPromotedProduct(String category);

    Optional<Product> findByTitle(String title);
}
