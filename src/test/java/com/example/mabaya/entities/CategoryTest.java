package com.example.mabaya.entities;

import com.example.mabaya.consts.ValidationMsg;
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
    void TestEmptyNameConstraintViolation() {
        Category categoryEmptyName = new Category();
        categoryEmptyName.setName("");
        Set<ConstraintViolation<Category>> violationsEmptyName = validator.validate(categoryEmptyName);
        assertFalse(violationsEmptyName.isEmpty());
        assertEquals(ValidationMsg.SIZE_CONSTRAINT_NAME_2_25,violationsEmptyName.iterator().next().getMessage());
    }

    @Test
    void TestShortNameConstraintViolation(){
        Category categoryShortName = new Category();
        categoryShortName.setName("1");
        Set<ConstraintViolation<Category>> violationsShortName = validator.validate(categoryShortName);
        assertFalse(violationsShortName.isEmpty());
        assertEquals(ValidationMsg.SIZE_CONSTRAINT_NAME_2_25,violationsShortName.iterator().next().getMessage());
    }

    @Test
    void TestLongNameConstraintViolation(){
        Category categoryLongName = new Category();
        categoryLongName.setName("1111111111111111111111111111111111111111111111111111111111");
        Set<ConstraintViolation<Category>> violationsLongName = validator.validate(categoryLongName);
        assertFalse(violationsLongName.isEmpty());
        assertEquals(ValidationMsg.SIZE_CONSTRAINT_NAME_2_25,violationsLongName.iterator().next().getMessage());
    }


}
