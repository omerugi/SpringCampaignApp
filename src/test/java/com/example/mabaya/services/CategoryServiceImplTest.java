package com.example.mabaya.services;

import com.example.mabaya.consts.ValidationMsg;
import com.example.mabaya.dto.CategoryDTO;
import com.example.mabaya.entities.Category;
import com.example.mabaya.entities.Product;
import com.example.mabaya.exeption.AppValidationException;
import com.example.mabaya.repositories.CategoryRepo;
import com.example.mabaya.servises.impls.CategoryServiceImpl;
import com.example.mabaya.utils.CategoryUtils;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class CategoryServiceImplTest {

    @Mock
    private CategoryRepo categoryRepo;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Test
    void testUpsertCategory() {
        CategoryDTO categoryDTO = new CategoryDTO();
        Category expectedCategory = new Category();
        when(categoryRepo.save(any(Category.class))).thenReturn(expectedCategory);

        Category actualCategory = categoryService.upsert(categoryDTO);

        assertEquals(expectedCategory, actualCategory);
        verify(categoryRepo).save(any(Category.class));
    }

    @Test
    void testGetCategoryById() {
        Long id = 1L;
        Category expectedCategory = new Category();
        when(categoryRepo.findById(id)).thenReturn(Optional.of(expectedCategory));

        Optional<Category> actualCategoryOptional = categoryService.getByid(id);

        assertTrue(actualCategoryOptional.isPresent());
        assertEquals(expectedCategory, actualCategoryOptional.get());
    }

    @Test
    void testGetCategoryByName() {
        String name = "Test Category";
        Category expectedCategory = new Category();
        when(categoryRepo.findByName(name)).thenReturn(Optional.of(expectedCategory));

        Optional<Category> actualCategoryOptional = categoryService.getByName(name);

        assertTrue(actualCategoryOptional.isPresent());
        assertEquals(expectedCategory, actualCategoryOptional.get());
    }

    @Test
    void testDeleteCategoryById() {
        Long id = 1L;
        Category category = new Category();
        when(categoryRepo.findById(id)).thenReturn(Optional.of(category));
        categoryService.deleteById(id);
        verify(categoryRepo).deleteById(id);
    }

    @Test
    void testDeleteCategoryByIdException() {
        Long id = 1L;
        Category category = new Category();
        Product product = new Product();
        product.setProductSerialNumber("1111");
        category.addProduct(product);
        when(categoryRepo.findById(id)).thenReturn(Optional.of(category));

        Exception exception = assertThrows(AppValidationException.class, () -> categoryService.deleteById(id));

        assertTrue(exception.getMessage().contains(ValidationMsg.cannotDeleteAttachedEntity(id, "111")));
    }
}
