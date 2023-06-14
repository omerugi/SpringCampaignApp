package com.example.mabaya.controllers;


import com.example.mabaya.consts.ValidationMsg;
import com.example.mabaya.dto.ProductDTO;
import com.example.mabaya.dto.projections.TopProductProjection;
import com.example.mabaya.entities.Campaign;
import com.example.mabaya.entities.Product;
import com.example.mabaya.exeption.AppValidationException;
import com.example.mabaya.repositories.CampaignRepo;
import com.example.mabaya.repositories.CategoryRepo;
import com.example.mabaya.repositories.ProductRepo;
import com.example.mabaya.servises.interfaces.ProductService;
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
class ProductControllerIntegrationTest {

    @Autowired
    ProductController productController;
    @Autowired
    ProductService productService;
    @Autowired
    ProductRepo productRepo;
    @Autowired
    CampaignRepo campaignRepo;
    @Autowired
    CategoryRepo categoryRepo;

    @Test
    void TestCreateNewProduct() {
        String productSerialNumber = "newProduct11111";
        String title = "New Product";
        double price = 14.4;
        String categoryName = "aa";
        ProductDTO productDTO = new ProductDTO();
        productDTO.setProductSerialNumber(productSerialNumber);
        productDTO.setTitle(title);
        productDTO.setPrice(price);
        productDTO.setCategoryName(categoryName);

        ResponseEntity<Product> productResponseEntity = productController.upsert(productDTO);
        assertEquals(HttpStatus.CREATED,productResponseEntity.getStatusCode());
        assertNotNull(productResponseEntity.getBody());
        Product productSaved = productResponseEntity.getBody();

        assertEquals(productDTO.getProductSerialNumber(),productSaved.getProductSerialNumber());
        assertEquals(productDTO.getTitle(),productSaved.getTitle());
        assertEquals(productDTO.getPrice(),productSaved.getPrice());
        assertEquals(productDTO.getCategoryName(),productSaved.getCategory().getName());

        Optional<Product> optionalProduct = productRepo.findById(productSaved.getProductSerialNumber());
        assertTrue(optionalProduct.isPresent());
        Product productFromDB = optionalProduct.get();
        assertEquals(productDTO.getProductSerialNumber(),productFromDB.getProductSerialNumber());
        assertEquals(productDTO.getTitle(),productFromDB.getTitle());
        assertEquals(productDTO.getPrice(),productFromDB.getPrice());
        assertEquals(productDTO.getCategoryName(),productFromDB.getCategory().getName());
    }

    @Test
    void TestUpdateNewProduct() {
        String productSerialNumber = "1";
        String title = "Update Product";
        double price = 14.4;
        String categoryName = "cc";
        ProductDTO productDTO = new ProductDTO();
        productDTO.setProductSerialNumber(productSerialNumber);
        productDTO.setTitle(title);
        productDTO.setPrice(price);
        productDTO.setCategoryName(categoryName);

        ResponseEntity<Product> productResponseEntity = productController.upsert(productDTO);
        assertEquals(HttpStatus.OK,productResponseEntity.getStatusCode());
        assertNotNull(productResponseEntity.getBody());
        Product productSaved = productResponseEntity.getBody();

        assertEquals(productDTO.getProductSerialNumber(),productSaved.getProductSerialNumber());
        assertEquals(productDTO.getTitle(),productSaved.getTitle());
        assertEquals(productDTO.getPrice(),productSaved.getPrice());
        assertEquals(productDTO.getCategoryName(),productSaved.getCategory().getName());

        Optional<Product> optionalProduct = productRepo.findById(productDTO.getProductSerialNumber());
        assertTrue(optionalProduct.isPresent());
        Product productFromDB = optionalProduct.get();
        assertEquals(productDTO.getProductSerialNumber(),productFromDB.getProductSerialNumber());
        assertEquals(productDTO.getTitle(),productFromDB.getTitle());
        assertEquals(productDTO.getPrice(),productFromDB.getPrice());
        assertEquals(productDTO.getCategoryName(),productFromDB.getCategory().getName());
    }

    @Test
    void TestServeAd(){
        // Category "cc" Have 2 products - "6" with camp "11" bid 400, and "3" with two camp: "22" bid 150 and "33" bid 10000 but inactive.
        ResponseEntity<TopProductProjection> responseTopProductProjectionCC = productController.serveAd("cc");
        assertNotNull(responseTopProductProjectionCC.getBody());
        assertEquals(HttpStatus.OK, responseTopProductProjectionCC.getStatusCode());
        TopProductProjection tppCase1 = responseTopProductProjectionCC.getBody();
        assertEquals(400.0, tppCase1.getBid());
        assertEquals("6", tppCase1.getProduct_serial_number());
        assertEquals("cc", tppCase1.getCategory());
        assertEquals("p6", tppCase1.getTitle());
        assertEquals(23.0, tppCase1.getPrice());

        // Category "ee" Have 1 product "8" with 3 camp : "22" bid 150 "33" bid 10000 but inactive, and "44" bid 300
        ResponseEntity<TopProductProjection> responseTopProductProjectionDD = productController.serveAd("ee");
        assertNotNull(responseTopProductProjectionDD.getBody());
        assertEquals(HttpStatus.OK, responseTopProductProjectionDD.getStatusCode());
        TopProductProjection tppCase2 = responseTopProductProjectionDD.getBody();
        assertEquals(300.0, tppCase2.getBid());
        assertEquals("8", tppCase2.getProduct_serial_number());
        assertEquals("ee", tppCase2.getCategory());
        assertEquals("p8", tppCase2.getTitle());
        assertEquals(29.99, tppCase2.getPrice());
    }

    @Test
    void TestServeAdNotFromAskedCategory(){
        // Category "aa" Have 2 products - "1" inactive, and "4" with camp "33" bid 10000 but inactive.
        ResponseEntity<TopProductProjection> responseTopProductProjectionAA = productController.serveAd("aa");
        assertNotNull(responseTopProductProjectionAA.getBody());
        assertEquals(HttpStatus.OK, responseTopProductProjectionAA.getStatusCode());
        TopProductProjection tppCase1 = responseTopProductProjectionAA.getBody();
        assertNotEquals("cc", tppCase1.getCategory());

        // Category "dd" have no product but still we get some answer.
        ResponseEntity<TopProductProjection> responseTopProductProjectionDD = productController.serveAd("dd");
        assertNotNull(responseTopProductProjectionDD.getBody());
        assertEquals(HttpStatus.OK, responseTopProductProjectionAA.getStatusCode());
        TopProductProjection tppCase2 = responseTopProductProjectionDD.getBody();
        assertNotEquals("dd", tppCase2.getCategory());
    }

    @Test
    void TestServeAdInvalidCategory(){
        Exception exception = assertThrows(AppValidationException.class, () -> productController.serveAd("ww"));
        assertTrue(exception.getMessage().contains(ValidationMsg.NOT_FOUND_CATEGORY_NAME));
    }

    @Test
    void TestGetBySerialNumber(){
        ResponseEntity<Product> responseProduct1 = productController.getBySerialNumber("1");
        assertNotNull(responseProduct1.getBody());
        assertEquals(HttpStatus.OK, responseProduct1.getStatusCode());
        Product product1 = responseProduct1.getBody();
        assertEquals("1",product1.getProductSerialNumber());
        assertEquals("p1",product1.getTitle());
        assertEquals(20.0,product1.getPrice());
        assertNotNull(product1.getCategory());
        assertEquals(111L,product1.getCategory().getId());
        assertEquals("aa",product1.getCategory().getName());
    }

    @Test
    void TestGetBySerialNumberNotExists(){
        ResponseEntity<Product> responseProduct = productController.getBySerialNumber("88888");
        assertNull(responseProduct.getBody());
        assertEquals(HttpStatus.NOT_FOUND, responseProduct.getStatusCode());
    }

    @Test
    @Transactional
    void TestDeleteBySerialNumber(){
        Optional<Product> optionalProductB4 = productRepo.findById("1");
        assertTrue(optionalProductB4.isPresent());
        Set<Long> productCampaigns = optionalProductB4.get().getCampaigns().stream().map(Campaign::getId).collect(Collectors.toSet());
        Long catId = optionalProductB4.get().getCategory().getId();

        productController.deleteBySerialNumber("1");
        Optional<Product> optionalProduct = productRepo.findById("1");
        assertFalse(optionalProduct.isPresent());
        assertEquals(productCampaigns.size(),campaignRepo.findAllById(productCampaigns).size());
        assertTrue(categoryRepo.existsById(catId));
    }

    @Test
    void TestDeleteNotInDB(){
        Exception exception = assertThrows(AppValidationException.class, () ->  productController.deleteBySerialNumber("88888"));
        assertTrue(exception.getMessage().contains(ValidationMsg.NOT_FOUND_PSN));
    }

}
