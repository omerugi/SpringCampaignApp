package com.example.mabaya.repositories;


import com.example.mabaya.MabayaApplication;
import com.example.mabaya.consts.ValidationMsg;
import com.example.mabaya.dto.projections.TopProductProjection;
import com.example.mabaya.entities.Campaign;
import com.example.mabaya.entities.Product;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@ContextConfiguration(classes = MabayaApplication.class)
@RunWith(SpringRunner.class)
@DataJpaTest
@SqlGroup({@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {"classpath:schema.sql", "classpath:data.sql"}),
        @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:drop.sql")})
class ProductRepoTest {

    ProductRepo productRepo;
    CategoryRepo categoryRepo;

    @Autowired
    public void setProductRepo(ProductRepo productRepo){
        this.productRepo = productRepo;
    }

    @Autowired
    public void setCategoryRepo(CategoryRepo categoryRepo){
        this.categoryRepo = categoryRepo;
    }

    @PersistenceContext
    EntityManager entityManager;


    public Product getProduct(String title, String serialNumber){
        Product product = new Product();
        product.setTitle(title);
        product.setProductSerialNumber(serialNumber);
        product.setCategory(categoryRepo.findById(111L).get());
        return product;
    }

    @Test
    @Transactional
    void TestFindTopPromotedProduct() throws Exception {
        String categoryWithNoTopProduct = "aa";
        String categoryNotExist = "xx";
        String validCategory = "bb";
        Map<String,Product> productMap = StreamSupport.stream(productRepo.findAll().spliterator(),false)
                .collect(Collectors.toMap(Product::getProductSerialNumber,prod->prod));

        TopProductProjection tppCategoryWithNoTopProduct = productRepo.findTopPromotedProduct(categoryWithNoTopProduct).orElseThrow(Exception::new);
        TopProductProjection tppCategoryNotExist = productRepo.findTopPromotedProduct(categoryNotExist).orElseThrow(Exception::new);
        TopProductProjection tppValidCategory = productRepo.findTopPromotedProduct(validCategory).orElseThrow(Exception::new);


        assertNotSame(productMap.get(tppCategoryWithNoTopProduct.getProduct_serial_number()).getCategory().getName(), categoryWithNoTopProduct);
        assertNotSame(productMap.get(tppCategoryNotExist.getProduct_serial_number()).getCategory().getName(), categoryNotExist);
        assertNotSame(productMap.get(tppValidCategory.getProduct_serial_number()).getCategory().getName(), validCategory);

        TypedQuery<Product> query = entityManager.createQuery(
                "SELECT p FROM Product p WHERE p.category.name = :category", Product.class);
        query.setParameter("category", validCategory);
        List<Product> productListByCategory = query.getResultList();
        productListByCategory.sort(Comparator.comparingDouble((Product p) -> p.getCampaigns().stream().mapToDouble(Campaign::getBid).max().orElse(0)).reversed());
        assertSame(productListByCategory.get(0).getProductSerialNumber(), productMap.get(tppValidCategory.getProduct_serial_number()).getProductSerialNumber());
    }

    @Test
    void TestSaveProductSuccesses() {
        Product product = getProduct("test","11test");
        productRepo.saveAndFlush(product);
        Product foundProduct = entityManager.find(Product.class, product.getProductSerialNumber());
        assertEquals(product.getProductSerialNumber(), foundProduct.getProductSerialNumber());
    }

    @Test
    void TestSaveProductNullTitle() {
        Product categoryNullTitle = getProduct(null,"s11");
        ConstraintViolationException thrownNullTitle = assertThrows(
                ConstraintViolationException.class,() -> productRepo.saveAndFlush(categoryNullTitle));
        assertTrue(thrownNullTitle.getMessage().contains(ValidationMsg.NULL_TITLE));
    }

    @Test
    void TestSaveProductShortTitle() {
        Product productShortTitle = getProduct("s","s11");
        ConstraintViolationException thrownShortTitle = assertThrows(
                ConstraintViolationException.class,
                () -> productRepo.saveAndFlush(productShortTitle));
        assertTrue(thrownShortTitle.getMessage().contains(ValidationMsg.SIZE_CONSTRAINT_TITLE_2_25));
    }

    @Test
    void TestSaveProductLongTitle() {
        Product productLongTitle = getProduct("this is a really long product title to test","s-12");
        ConstraintViolationException thrownLongTitle = assertThrows(
                ConstraintViolationException.class,
                () -> productRepo.saveAndFlush(productLongTitle));
        assertTrue(thrownLongTitle.getMessage().contains(ValidationMsg.SIZE_CONSTRAINT_TITLE_2_25));
    }

    @Test
    void TestSaveProductEmptyTitle() {
        Product productLongTitle = getProduct("","s-12");
        ConstraintViolationException thrownLongTitle = assertThrows(
                ConstraintViolationException.class,
                () -> productRepo.saveAndFlush(productLongTitle));
        assertTrue(thrownLongTitle.getMessage().contains(ValidationMsg.SIZE_CONSTRAINT_TITLE_2_25));
    }


    @Test
    void TestSaveProductNegativePrice() {
        Product productNegativePrice = getProduct("test","s11");
        productNegativePrice.setPrice(-100.0);
        ConstraintViolationException thrown = assertThrows(
                ConstraintViolationException.class,
                () -> productRepo.saveAndFlush(productNegativePrice));
        assertTrue(thrown.getMessage().contains(ValidationMsg.NUM_PRICE_NEGATIVE));
    }

    @Test
    void TestSaveProductNullSerialNumber() {
        Product productNullSerialNumber = getProduct("test",null);
        assertThrows(JpaSystemException.class, () -> productRepo.saveAndFlush(productNullSerialNumber));
    }

    @Test
    void TestSaveProductNullCategory() {
        Product productNullCategory = new Product();
        productNullCategory.setTitle("Valid Title");
        productNullCategory.setProductSerialNumber("SN345");

        ConstraintViolationException thrown = assertThrows(
                ConstraintViolationException.class,
                () ->  productRepo.saveAndFlush(productNullCategory));
        assertTrue(thrown.getMessage().contains(ValidationMsg.NULL_CATEGORY));
    }

    @Test
    void TestFindProductById() {
        Optional<Product> productToSaveFind = productRepo.findById("1");
        Optional<Product> fromDataFind = productRepo.findById("2");
        Optional<Product> noFind = productRepo.findById("xxxx");

        assertTrue(productToSaveFind.isPresent());
        assertTrue(fromDataFind.isPresent());
        assertFalse(noFind.isPresent());
        assertEquals("1",productToSaveFind.get().getProductSerialNumber());
        assertEquals("2",fromDataFind.get().getProductSerialNumber());
    }

    @Test
    void testFindProductByTitleFound(){
        Optional<Product> foundProduct = productRepo.findByTitle("p1");
        assertTrue(foundProduct.isPresent());
        assertEquals("p1",foundProduct.get().getTitle());
    }

    @Test
    void testFindProductByTitleNotFound(){
        Optional<Product> foundProduct = productRepo.findByTitle("Made up product title that is not in DB");
        assertFalse(foundProduct.isPresent());
    }

}



