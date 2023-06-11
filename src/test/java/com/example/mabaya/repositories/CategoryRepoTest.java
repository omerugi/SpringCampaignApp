package com.example.mabaya.repositories;

import com.example.mabaya.MabayaApplication;
import com.example.mabaya.entities.Category;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ContextConfiguration(classes = MabayaApplication.class)
@RunWith(SpringRunner.class)
@DataJpaTest
@SqlGroup({@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {"classpath:schema.sql","classpath:data.sql"}),
        @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:drop.sql")})
class CategoryRepoTest {


    @Autowired
    CategoryRepo categoryRepo;

    @PersistenceContext
    EntityManager entityManager;


    private void clearAndFlush(){
        entityManager.clear();
        entityManager.flush();
    }

    @Test
    void TestSaveCategory() {
        Category categoryToSave = new Category();
        categoryToSave.setName("test-cat");
        categoryRepo.save(categoryToSave);

        clearAndFlush();
        Category foundCategory = entityManager.find(Category.class, categoryToSave.getId());

        assertEquals(foundCategory.getId(), categoryToSave.getId());
    }

    @Test
    void testFindCategoryById() {
        Long idToFind = 111L;
        Optional<Category> CategoryFound = categoryRepo.findById(idToFind);

        assertTrue(CategoryFound.isPresent());
        assertEquals(idToFind, CategoryFound.get().getId());
    }

    @Test
    void testFindCategoryByName(){
        Category categoryToSave = new Category();
        categoryToSave.setName("test-cat");
        categoryRepo.save(categoryToSave);
        clearAndFlush();

        Optional<Category> foundCategory = categoryRepo.findByName(categoryToSave.getName());
        assertTrue(foundCategory.isPresent());
        assertEquals(foundCategory.get().getId(), categoryToSave.getId());
    }

}
