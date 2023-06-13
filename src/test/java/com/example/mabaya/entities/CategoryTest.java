package com.example.mabaya.entities;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CategoryTest {

    @Autowired
    private Validator validator;

    @Test
    void testNameConstraintViolation() {
        Category category = new Category();
        category.setName("");
        Set<ConstraintViolation<Category>> violations = validator.validate(category);
        assertFalse(violations.isEmpty());
        assertEquals("Name should be between 2-25 chars", violations.iterator().next().getMessage());
    }

    @Test
    void testNameIsTooLongConstraintViolation() {
        Category category = new Category();
        category.setName("This name is definitely more than twenty-five characters long");
        Set<ConstraintViolation<Category>> violations = validator.validate(category);
        assertFalse(violations.isEmpty());
        assertEquals("Name should be between 2-25 chars", violations.iterator().next().getMessage());
    }
}
