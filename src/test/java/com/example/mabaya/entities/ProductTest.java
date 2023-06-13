package com.example.mabaya.entities;

import com.example.mabaya.consts.ValidationMsg;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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

    private Product getProduct(String psn, String title){
        Product product = new Product();
        product.setProductSerialNumber(psn);
        product.setTitle(title);
        product.setActive(true);
        product.setCategory(getCategory());
        return product;
    }

    @Test
    void TestEmptyProductSerialNumberConstraintViolation() {
        Product product = getProduct("", "title");
        Set<ConstraintViolation<Product>> violations = validator.validate(product);
        assertFalse(violations.isEmpty());
        assertEquals(ValidationMsg.INVALID_PSN, violations.iterator().next().getMessage());
    }

    @Test
    void TestSpecialCharProductSerialNumberConstraintViolation() {
        Product product = getProduct(">???111vv","title");
        Set<ConstraintViolation<Product>> violations = validator.validate(product);
        assertFalse(violations.isEmpty());
        assertEquals(ValidationMsg.INVALID_PSN, violations.iterator().next().getMessage());
    }

    @Test
    void TestEmptyTitleConstraintViolation() {
        Product product = getProduct("psn1","");
        Set<ConstraintViolation<Product>> violations = validator.validate(product);
        assertFalse(violations.isEmpty());
        assertEquals(ValidationMsg.SIZE_CONSTRAINT_TITLE_2_25, violations.iterator().next().getMessage());
    }

    @Test
    void TestShortTitleConstraintViolation() {
        Product product = getProduct("psn1","1");
        Set<ConstraintViolation<Product>> violations = validator.validate(product);
        assertFalse(violations.isEmpty());
        assertEquals(ValidationMsg.SIZE_CONSTRAINT_TITLE_2_25, violations.iterator().next().getMessage());
    }

    @Test
    void TestLongTitleConstraintViolation() {
        Product product = getProduct("psn1","111111111111111111111111111111111111");
        Set<ConstraintViolation<Product>> violations = validator.validate(product);
        assertFalse(violations.isEmpty());
        assertEquals(ValidationMsg.SIZE_CONSTRAINT_TITLE_2_25, violations.iterator().next().getMessage());
    }

    @Test
    void TestNullTitleConstraintViolation() {
        Product product = getProduct("psn1",null);
        Set<ConstraintViolation<Product>> violations = validator.validate(product);
        assertFalse(violations.isEmpty());
        assertEquals(ValidationMsg.NULL_TITLE, violations.iterator().next().getMessage());
    }

    @Test
    void TestNegativePriceConstraintViolation() {
        Product product = getProduct("test","title");
        product.setPrice(-50.0);

        Set<ConstraintViolation<Product>> violations = validator.validate(product);

        assertFalse(violations.isEmpty());
        assertEquals(ValidationMsg.NUM_PRICE_NEGATIVE, violations.iterator().next().getMessage());
    }

    @Test
    void TestNullCategoryViolation() {
        Product product = getProduct("test","title");
        product.setCategory(null);

        Set<ConstraintViolation<Product>> violations = validator.validate(product);

        assertFalse(violations.isEmpty());
        assertEquals(ValidationMsg.NULL_CATEGORY, violations.iterator().next().getMessage());
    }
}
