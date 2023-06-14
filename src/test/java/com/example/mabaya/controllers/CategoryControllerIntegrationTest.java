package com.example.mabaya.controllers;

import com.example.mabaya.consts.ValidationMsg;
import com.example.mabaya.dto.CategoryDTO;
import com.example.mabaya.entities.Category;
import com.example.mabaya.entities.Product;
import com.example.mabaya.exeption.AppValidationException;
import com.example.mabaya.repositories.CategoryRepo;
import com.example.mabaya.repositories.ProductRepo;
import com.example.mabaya.servises.interfaces.CategoryService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@SqlGroup({@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {"classpath:schema.sql", "classpath:data.sql"}),
        @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:drop.sql")})
class CategoryControllerIntegrationTest {


    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryController categoryController;

    @Autowired
    private CategoryRepo categoryRepo;

    @Autowired
    private ProductRepo productRepo;

    @Test
    void TestCreateCategory() {
        String name = "test";
        CategoryDTO categoryDTO = new CategoryDTO(name);

        ResponseEntity<Category> categoryResponseEntity = categoryController.upsert(categoryDTO);
        assertNotNull(categoryResponseEntity.getBody());
        assertEquals(HttpStatus.CREATED, categoryResponseEntity.getStatusCode());
        Category newCategory = categoryResponseEntity.getBody();
        assertEquals(categoryDTO.getName(), newCategory.getName());

        Optional<Category> optionalCategory = categoryRepo.findByName(categoryDTO.getName());
        assertTrue(optionalCategory.isPresent());
        Category categoryFromDB = optionalCategory.get();
        assertEquals(categoryFromDB.getName(), categoryDTO.getName());
    }

    @Test
    @Transactional
    void TestUpdateCategory() {
        Optional<Category> opCategoryB4 = categoryRepo.findById(111L);
        assertTrue(opCategoryB4.isPresent());
        Category categoryB4 = opCategoryB4.get();

        Long id = 111L;
        String name = "test-update";
        CategoryDTO categoryDTO = new CategoryDTO(id, name);

        assertEquals(categoryDTO.getId(), categoryB4.getId());
        assertNotEquals(categoryDTO.getName(), categoryB4.getName());

        ResponseEntity<Category> categoryResponseEntity = categoryController.upsert(categoryDTO);
        assertNotNull(categoryResponseEntity.getBody());
        assertEquals(HttpStatus.OK, categoryResponseEntity.getStatusCode());
        assertEquals(categoryDTO.getName(), categoryB4.getName());

        Optional<Category> optionalCategory = categoryRepo.findById(categoryDTO.getId());
        assertTrue(optionalCategory.isPresent());
        Category categoryFromDB = optionalCategory.get();
        assertEquals(categoryDTO.getId(), categoryFromDB.getId());
        assertEquals(categoryDTO.getName(), categoryFromDB.getName());
    }

    @Test
    void TestGetById() {
        ResponseEntity<Category> categoryResponseEntity = categoryController.getById(111L);
        assertNotNull(categoryResponseEntity.getBody());
        assertEquals(HttpStatus.OK, categoryResponseEntity.getStatusCode());
        assertEquals(111L,categoryResponseEntity.getBody().getId());
        assertEquals("aa", categoryResponseEntity.getBody().getName());
    }

    @Test
    void TestGetByIdNotExists() {
        ResponseEntity<Category> categoryResponseEntity = categoryController.getById(6L);
        assertNull(categoryResponseEntity.getBody());
        assertEquals(HttpStatus.NOT_FOUND, categoryResponseEntity.getStatusCode());
    }

    @Test
    @Transactional
    void TestDeleteById() {
        Optional<Category> optionalCategoryB4 = categoryRepo.findById(444L);
        assertTrue(optionalCategoryB4.isPresent());
        Set<String> productsPsn = optionalCategoryB4.get().getProducts().stream().map(Product::getProductSerialNumber).collect(Collectors.toSet());

        categoryController.deleteById(444L);
        Optional<Category> optionalCategory = categoryRepo.findById(444L);
        assertFalse(optionalCategory.isPresent());
        assertEquals(productsPsn.size(),productRepo.findAllById(productsPsn).size());
    }

    @Test
    void TestDeleteIdNotInDB() {
        Exception exception = assertThrows(AppValidationException.class, () -> categoryController.deleteById(666L));
        assertTrue(exception.getMessage().contains(ValidationMsg.NOT_FOUND_ID));
    }

    @Test
    @Transactional
    void TestDeleteAttachedProducts() {
        Exception exception = assertThrows(AppValidationException.class, () -> categoryController.deleteById(111L));
        assertTrue(exception.getMessage().contains(ValidationMsg.CANNOT_DELETE_CATEGORY_ATTACHED_PRODUCTS));
    }

}
