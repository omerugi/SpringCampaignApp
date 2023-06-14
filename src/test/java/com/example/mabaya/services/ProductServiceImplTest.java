package com.example.mabaya.services;

import com.example.mabaya.consts.ValidationMsg;
import com.example.mabaya.dto.ProductDTO;
import com.example.mabaya.dto.projections.TopProductProjection;
import com.example.mabaya.entities.Category;
import com.example.mabaya.entities.Product;
import com.example.mabaya.exeption.AppValidationException;
import com.example.mabaya.repositories.ProductRepo;
import com.example.mabaya.servises.impls.ProductServiceImpl;
import com.example.mabaya.servises.interfaces.CategoryService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class ProductServiceImplTest {

    @Mock
    private ProductRepo productRepo;

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private ProductServiceImpl productService;

    @Test
    void TestUpsert() {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setCategoryName("cat-test");

        Category category = new Category();
        category.setName("cat-test");

        Product product = new Product();
        product.setCategory(category);

        when(categoryService.getByName("cat-test")).thenReturn(Optional.of(category));
        when(productRepo.save(any(Product.class))).thenReturn(product);

        Product result = productService.upsert(productDTO);
        assertNotNull(result);
        assertEquals(product, result);
    }

    @Test
    void TestUpsertInvalidCategory(){
        ProductDTO productDTO = new ProductDTO();
        productDTO.setCategoryName("cat-test");
        when(categoryService.getByName("cat-test")).thenReturn(Optional.empty());
        Exception exception = assertThrows(AppValidationException.class, () ->
                productService.upsert(productDTO));
        assertTrue(exception.getMessage().contains(ValidationMsg.NOT_FOUND_CATEGORY_NAME));
    }

    @Test
    void TestGetHighestBiddedProduct() {
        TopProductProjection projection = mock(TopProductProjection.class);
        when(categoryService.getByName("cat-test")).thenReturn(Optional.of(new Category()));
        when(productRepo.findTopPromotedProduct("cat-test")).thenReturn(Optional.of(projection));

        Optional<TopProductProjection> result = productService.getHighestBiddedProductByCategorty("cat-test");
        assertTrue(result.isPresent());
        assertEquals(projection, result.get());
    }

    @Test
    void TestGetHighestBiddedProductInvalidCategory() {
        when(categoryService.getByName("bad-cat")).thenReturn(Optional.empty());
        Exception exception = assertThrows(AppValidationException.class, () ->
                productService.getHighestBiddedProductByCategorty("bad-cat"));
        assertTrue(exception.getMessage().contains(ValidationMsg.NOT_FOUND_CATEGORY_NAME));
    }

    @Test
    void TestGetProductsByValidSerialNumbers() {
        Set<String> serialNumbers = new HashSet<>(Arrays.asList("111", "222"));

        Product product1 = new Product();
        product1.setProductSerialNumber("111");

        Product product2 = new Product();
        product2.setProductSerialNumber("222");

        when(productRepo.findAllById(any())).thenReturn(Arrays.asList(product1, product2));

        Set<Product> result = productService.getProductsBySerialNumbers(serialNumbers);
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void TestGetProductsByFakeSerialNumbers() {
        Set<String> serialNumbers = new HashSet<>(Arrays.asList("111", "222"));

        Product product1 = new Product();
        product1.setProductSerialNumber("111");

        Product product2 = new Product();
        product2.setProductSerialNumber("222");

        when(productRepo.findAllById(Collections.singleton(anyString()))).thenReturn(List.of(product1));

        Exception exception = assertThrows(AppValidationException.class, () ->
                productService.getProductsBySerialNumbers(serialNumbers));
        assertTrue(exception.getMessage().contains(ValidationMsg.NOT_FOUND_PSN));
    }

    @Test
    void TestGetDoesExistBySerialNumberTrue(){
        when(productRepo.existsById(anyString())).thenReturn(true);
        assertTrue(productService.doesExistBySerialNumber("11"));
        assertTrue(productService.doesExistBySerialNumber("113"));
        assertTrue(productService.doesExistBySerialNumber("1133"));
        assertTrue(productService.doesExistBySerialNumber("11333"));
    }

    @Test
    void TestGetDoesExistBySerialNumberFalse(){
        when(productRepo.existsById(anyString())).thenReturn(false);
        assertFalse(productService.doesExistBySerialNumber("11"));
        assertFalse(productService.doesExistBySerialNumber("113"));
        assertFalse(productService.doesExistBySerialNumber("1133"));
        assertFalse(productService.doesExistBySerialNumber("11333"));
    }
}
