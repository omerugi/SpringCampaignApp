package com.example.mabaya.repositories;

import com.example.mabaya.MabayaApplication;
import com.example.mabaya.consts.ValidationMsg;
import com.example.mabaya.entities.Category;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.validation.ConstraintViolationException;
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


    CategoryRepo categoryRepo;
    @Autowired
    public void setCategoryRepo(CategoryRepo categoryRepo){
        this.categoryRepo = categoryRepo;
    }

    @PersistenceContext
    EntityManager entityManager;


    @Test
    void testFindCategoryById() {
        Long idToFind = 111L;
        Optional<Category> CategoryFound = categoryRepo.findById(idToFind);

        assertTrue(CategoryFound.isPresent());
        assertEquals(idToFind, CategoryFound.get().getId());
    }

    @Test
    void testFindCategoryByNameFound(){
        Category categoryToSave = new Category();
        categoryToSave.setName("test-cat");
        categoryRepo.saveAndFlush(categoryToSave);

        Optional<Category> foundCategory = categoryRepo.findByName(categoryToSave.getName());
        assertTrue(foundCategory.isPresent());
        assertEquals(foundCategory.get().getId(), categoryToSave.getId());
    }

    @Test
    void testFindCategoryByNotFound(){
        Optional<Category> foundCategory = categoryRepo.findByName("Made up category that is not in DB");
        assertFalse(foundCategory.isPresent());
    }

    @Test
    void TestSaveCategorySuccesses() {
        Category categoryToSave = new Category();
        categoryToSave.setName("test-cat");
        categoryRepo.saveAndFlush(categoryToSave);

        Category foundCategory = entityManager.find(Category.class, categoryToSave.getId());
        assertEquals(foundCategory.getId(), categoryToSave.getId());
    }

    @Test
    void TestSaveCampaignFailShortName() {
        Category categoryShortName = new Category();
        categoryShortName.setName("T");
        ConstraintViolationException thrownShortName = assertThrows(
                ConstraintViolationException.class,
                () -> categoryRepo.saveAndFlush(categoryShortName));
        assertTrue(thrownShortName.getMessage().contains(ValidationMsg.SIZE_CONSTRAINT_NAME_2_25));
    }

    @Test
    void TestSaveCampaignFailLongName() {
        Category categoryLongName = new Category();
        categoryLongName.setName("this is a really long name to test");
        ConstraintViolationException thrownLongName = assertThrows(
                ConstraintViolationException.class,
                () -> categoryRepo.saveAndFlush(categoryLongName));
        assertTrue(thrownLongName.getMessage().contains(ValidationMsg.SIZE_CONSTRAINT_NAME_2_25));
    }

    @Test
    void TestSaveCampaignFailEmptyName() {
        Category categoryEmptyName = new Category();
        categoryEmptyName.setName("");

        ConstraintViolationException thrownShortName = assertThrows(
                ConstraintViolationException.class,
                () -> categoryRepo.saveAndFlush(categoryEmptyName));
        assertTrue(thrownShortName.getMessage().contains(ValidationMsg.SIZE_CONSTRAINT_NAME_2_25));
    }

    @Test
    void TestSaveCategoryFailNullName(){
        Category categoryNullName = new Category();
        ConstraintViolationException thrownNullName = assertThrows(ConstraintViolationException.class,() -> {
            categoryRepo.saveAndFlush(categoryNullName);
        });
        assertTrue(thrownNullName.getMessage().contains(ValidationMsg.NULL_NAME));
    }

}
