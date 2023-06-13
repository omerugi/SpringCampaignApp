package com.example.mabaya.controllers;

import com.example.mabaya.consts.ValidationMsg;
import com.example.mabaya.dto.CategoryDTO;
import com.example.mabaya.entities.Category;
import com.example.mabaya.exeption.AppValidationException;
import com.example.mabaya.repositories.CategoryRepo;
import com.example.mabaya.servises.interfaces.CategoryService;
import com.github.javafaker.App;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

import java.util.Optional;

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

    @Test
    void TestCreateCategory() throws Exception {
        String name = "test";
        CategoryDTO categoryDTO = new CategoryDTO(name);

        ResponseEntity<Category> categoryResponseEntity = categoryController.upsert(categoryDTO);
        assertNotNull(categoryResponseEntity.getBody());
        assertEquals(HttpStatus.CREATED,categoryResponseEntity.getStatusCode());

        Category newCategory = categoryResponseEntity.getBody();
        assertEquals(categoryDTO.getName(), newCategory.getName());
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

        ResponseEntity<Category> categoryResponseEntity = categoryController.upsert(categoryDTO);
        assertNotNull(categoryResponseEntity.getBody());
        assertEquals(HttpStatus.OK,categoryResponseEntity.getStatusCode());
        assertEquals(categoryDTO.getName(), categoryB4.getName());
    }

    @Test
    void TestGetById() throws Exception{
        ResponseEntity<Category> categoryResponseEntity = categoryController.getById(111L);
        assertNotNull(categoryResponseEntity.getBody());
        assertEquals(HttpStatus.OK,categoryResponseEntity.getStatusCode());
        assertEquals("aa",categoryResponseEntity.getBody().getName());
    }

    @Test
    void TestGetByIdNotExists() throws Exception{
        try{
            categoryController.getById(111L);
        }catch (AppValidationException e){
            assertEquals(ValidationMsg.NOT_FOUND_ID,e.getMessage());
        }
    }

    @Test
    @Transactional
    void TestDeleteById(){
        Optional<Category> optionalCategoryB4 = categoryRepo.findById(444L);
        assertTrue(optionalCategoryB4.isPresent());
        categoryController.deleteById(444L);
        Optional<Category> optionalCategory = categoryRepo.findById(444L);
        assertFalse(optionalCategory.isPresent());
    }

    @Test
    void TestDeleteIdNotInDB() throws Exception {
        try{
            categoryController.deleteById(666L);
        }catch (AppValidationException e){
            assertEquals("666 "+ValidationMsg.NOT_FOUND_ID,e.getMessage());
        }
    }

    @Test
    @Transactional
    void TestDeleteAttachedProducts() {
        try{
            categoryController.deleteById(111L);
        }catch (AppValidationException e){
            assertTrue(e.getMessage().contains(ValidationMsg.CANNOT_DELETE_CATEGORY_ATTACHED_PRODUCTS));
        }
    }

}
