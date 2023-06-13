package com.example.mabaya.entities;

import com.example.mabaya.consts.ValidationMsg;
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

    private Category getCategory(){
        Category category = new Category();
        category.setName("test");
        return category;
    }

    @Test
    void testProductSerialNumberEmptyConstraintViolation() {
        Product product = new Product();
        product.setProductSerialNumber("");
        product.setTitle("Test Product");
        product.setPrice(50.0);
        product.setActive(true);
        product.setCategory(getCategory());

        Set<ConstraintViolation<Product>> violations = validator.validate(product);

        assertFalse(violations.isEmpty());
        assertEquals("Product Serial Number should be at lease size 1 and contain letters and digits", violations.iterator().next().getMessage());
    }

    @Test
    void testProductTitleEmptyConstraintViolation() {
        Product product = new Product();
        product.setProductSerialNumber("123456");
        product.setTitle("");
        product.setPrice(50.0);
        product.setActive(true);
        product.setCategory(getCategory());

        Set<ConstraintViolation<Product>> violations = validator.validate(product);

        assertFalse(violations.isEmpty());
        assertEquals("Title should be between 2-25 chars", violations.iterator().next().getMessage());
    }

    @Test
    void testProductPriceNegativeConstraintViolation() {
        Product product = new Product();
        product.setProductSerialNumber("123456");
        product.setTitle("Test Product");
        product.setPrice(-50.0);
        product.setActive(true);
        product.setCategory(getCategory());

        Set<ConstraintViolation<Product>> violations = validator.validate(product);

        assertFalse(violations.isEmpty());
        assertEquals("must be greater than or equal to 0.0", violations.iterator().next().getMessage());
    }

    @Test
    void testProductPriceNoCategoryViolation() {
        Product product = new Product();
        product.setProductSerialNumber("123456");
        product.setTitle("Test Product");
        product.setPrice(50.0);
        product.setActive(true);

        Set<ConstraintViolation<Product>> violations = validator.validate(product);

        assertFalse(violations.isEmpty());
        assertEquals("Must have a category", violations.iterator().next().getMessage());
    }
}
