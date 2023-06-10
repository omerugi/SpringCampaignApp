package com.example.mabaya.entities;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
class ProductTest {

    @Autowired
    private Validator validator;

    @Test
    void testProductSerialNumberEmptyConstraintViolation() {
        Product product = new Product();
        product.setProductSerialNumber("");
        product.setTitle("Test Product");
        product.setCategory("Test Category");
        product.setPrice(50.0);
        product.setActive(true);

        Set<ConstraintViolation<Product>> violations = validator.validate(product);

        assertFalse(violations.isEmpty());
        assertEquals("Product Serial Number cannot be empty", violations.iterator().next().getMessage());
    }

    @Test
    void testProductTitleEmptyConstraintViolation() {
        Product product = new Product();
        product.setProductSerialNumber("123456");
        product.setTitle("");
        product.setCategory("Test Category");
        product.setPrice(50.0);
        product.setActive(true);

        Set<ConstraintViolation<Product>> violations = validator.validate(product);

        assertFalse(violations.isEmpty());
        assertEquals("Title cannot be empty", violations.iterator().next().getMessage());
    }

    @Test
    void testProductCategoryEmptyConstraintViolation() {
        Product product = new Product();
        product.setProductSerialNumber("123456");
        product.setTitle("Test Product");
        product.setCategory("");
        product.setPrice(50.0);
        product.setActive(true);

        Set<ConstraintViolation<Product>> violations = validator.validate(product);

        assertFalse(violations.isEmpty());
        assertEquals("Category cannot be empty", violations.iterator().next().getMessage());
    }

    @Test
    void testProductPriceNegativeConstraintViolation() {
        Product product = new Product();
        product.setProductSerialNumber("123456");
        product.setTitle("Test Product");
        product.setCategory("Test Category");
        product.setPrice(-50.0);
        product.setActive(true);

        Set<ConstraintViolation<Product>> violations = validator.validate(product);

        assertFalse(violations.isEmpty());
        assertEquals("must be greater than or equal to 0.0", violations.iterator().next().getMessage());
    }
}
