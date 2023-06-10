package com.example.mabaya.repositories;


import com.example.mabaya.MabayaApplication;
import com.example.mabaya.dto.projections.TopProductProjection;
import com.example.mabaya.entities.Campaign;
import com.example.mabaya.entities.Product;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
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

    @Autowired
    ProductRepo productRepo;

    @PersistenceContext
    EntityManager entityManager;

    private void clearAndFlush(){
        entityManager.clear();
        entityManager.flush();
    }

    public Product getProduct(String title, String serialNumber){
        Product product = new Product();
        product.setTitle(title);
        product.setProductSerialNumber(serialNumber);
        product.setCategory("generic-category");
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


        assertNotSame(productMap.get(tppCategoryWithNoTopProduct.getProduct_serial_number()).getCategory(), categoryWithNoTopProduct);
        assertNotSame(productMap.get(tppCategoryNotExist.getProduct_serial_number()).getCategory(), categoryNotExist);
        assertNotSame(productMap.get(tppValidCategory.getProduct_serial_number()).getCategory(), validCategory);

        TypedQuery<Product> query = entityManager.createQuery(
                "SELECT p FROM Product p WHERE p.category = :category", Product.class);
        query.setParameter("category", validCategory);
        List<Product> productListByCategory = query.getResultList();
        productListByCategory.sort(Comparator.comparingDouble((Product p) -> p.getCampaigns().stream().mapToDouble(Campaign::getBid).max().orElse(0)).reversed());
        assertSame(productListByCategory.get(0).getProductSerialNumber(), productMap.get(tppValidCategory.getProduct_serial_number()).getProductSerialNumber());
    }


    @Test
    void TestSaveProduct() {
        Product product = new Product();
        product.setProductSerialNumber("123456");
        product.setTitle("Test Product");
        product.setCategory("Test Category");
        product.setPrice(50.0);
        product.setActive(true);

        productRepo.save(product);
        Product foundProduct = entityManager.find(Product.class, product.getProductSerialNumber());

        assertEquals(product.getProductSerialNumber(), foundProduct.getProductSerialNumber());
    }

    @Test
    void TestFindProductById() {
        Product productToSave = getProduct("test-find","find-1");

        productRepo.save(productToSave);
        Optional<Product> productToSaveFind = productRepo.findById(productToSave.getProductSerialNumber());
        Optional<Product> fromDataFind = productRepo.findById("2");
        Optional<Product> noFind = productRepo.findById("xxxx");

        assertTrue(productToSaveFind.isPresent());
        assertTrue(fromDataFind.isPresent());
        assertFalse(noFind.isPresent());
        assertEquals(productToSave.getProductSerialNumber(), productToSaveFind.get().getProductSerialNumber());
        assertEquals("2",fromDataFind.get().getProductSerialNumber());
    }


}
